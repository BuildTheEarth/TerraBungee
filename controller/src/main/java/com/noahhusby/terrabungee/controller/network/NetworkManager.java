/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * TerraBungeePacketManager.java
 */

/*
 * TerraBungee - Bungeecord Proxy
 * packets/TerraBungeePacketManager.java
 *
 * Author: Noah Husby
 */

package com.noahhusby.terrabungee.controller.network;

import com.google.gson.JsonObject;
import com.noahhusby.lib.data.JsonUtils;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.network.C2S.C2SResponsePacket;
import com.noahhusby.terrabungee.controller.network.P2C.P2CUpdatePlayersPacket;
import com.noahhusby.terrabungee.controller.network.S2C.*;
import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private static NetworkManager instance;

    public static NetworkManager getInstance() {
        if(instance == null) instance = new NetworkManager();
        return instance;
    }

    private final List<IS2CPacket> registeredServicePackets = new ArrayList<>();

    private NetworkManager() {
        registerServicePacket(new S2CKeepAlivePacket());
        registerServicePacket(new S2CAddStaticInstancePacket());
        registerServicePacket(new S2CRemoveStaticInstancePacket());
        registerServicePacket(new S2CServiceMessagePacket());
        registerServicePacket(new S2CSetServiceStatusPacket());
        registerServicePacket(new P2CUpdatePlayersPacket());
        registerServicePacket(new S2CRetrieveUncachedPlayerPacket());
        registerServicePacket(new S2CUpdateAttributeID());
    }

    private void registerServicePacket(IS2CPacket packet) {
        registeredServicePackets.add(packet);
    }

    /**
     * Will be executed upon incoming payload for Redis/Websocket
     * @param client Websocket client
     * @param p Raw string data
     */
    public void onIncomingPayload(WsContext client, String p) {
        JsonObject payload = JsonUtils.parseString(p).getAsJsonObject();
        String salt = payload.get("salt").getAsString();
        String id = payload.get("id").getAsString();
        String type = payload.get("type").getAsString();

        ServicePacket sp = new ServicePacket(client, id);
        JsonObject data = payload.get("data").getAsJsonObject();

        for(IS2CPacket s : registeredServicePackets) {
            if(s.getID().equalsIgnoreCase(type)) {
                Response response = new Response(sp, salt);
                s.onMessage(sp, data, response);
                send(new C2SResponsePacket(response));
            }
        }
    }

    public void send(IC2SPacket packet) {
        ServicePacket servicePacket = packet.getServicePacket();
        if(servicePacket == null) return;

        JsonObject payload = new JsonObject();
        payload.addProperty("type", packet.getID());
        payload.addProperty("id", servicePacket.getID());

        JsonObject packetData = new JsonObject();
        packet.getMessage(packetData);
        payload.add("data", packetData);

        servicePacket.getClient().send(TerraBungeeUtil.GSON.toJson(payload));
    }
}
