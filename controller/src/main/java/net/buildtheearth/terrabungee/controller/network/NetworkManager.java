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

package net.buildtheearth.terrabungee.controller.network;

import com.google.gson.JsonObject;
import com.noahhusby.lib.data.JsonUtils;
import io.javalin.websocket.WsContext;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.INetworkManager;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SResponsePacket;
import net.buildtheearth.terrabungee.controller.network.P2C.P2CUpdatePlayersPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CAddStaticInstancePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CKeepAlivePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CRemoveStaticInstancePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CRetrieveUncachedPlayerPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CServiceMessagePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CSetServiceStatusPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CUpdateAttributeID;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager implements INetworkManager {
    private static NetworkManager instance;

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    private final List<IS2CPacket> registeredServicePackets = new ArrayList<>();

    private NetworkManager() {
        register(new S2CKeepAlivePacket());
        register(new S2CAddStaticInstancePacket());
        register(new S2CRemoveStaticInstancePacket());
        register(new S2CServiceMessagePacket());
        register(new S2CSetServiceStatusPacket());
        register(new P2CUpdatePlayersPacket());
        register(new S2CRetrieveUncachedPlayerPacket());
        register(new S2CUpdateAttributeID());
    }

    public void onIncomingPayload(WsContext client, String p) {
        JsonObject payload = JsonUtils.parseString(p).getAsJsonObject();
        String salt = payload.get("salt").getAsString();
        String id = payload.get("id").getAsString();
        String type = payload.get("type").getAsString();

        ServicePacket sp = new ServicePacket(client, id);
        JsonObject data = payload.get("data").getAsJsonObject();

        for (IS2CPacket s : registeredServicePackets) {
            if (s.getID().equalsIgnoreCase(type)) {
                Response response = new Response(sp, salt);
                s.onMessage(sp, data, response);
                send(new C2SResponsePacket(response));
            }
        }
    }

    public void send(IC2SPacket packet) {
        ServicePacket servicePacket = packet.getServicePacket();
        if (servicePacket == null) {
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("type", packet.getID());
        payload.addProperty("id", servicePacket.getID());

        JsonObject packetData = new JsonObject();
        packet.getMessage(packetData);
        payload.add("data", packetData);

        servicePacket.getClient().send(TerraBungeeUtil.GSON.toJson(payload));
    }

    @Override
    public void register(IS2CPacket packet) {
        registeredServicePackets.add(packet);
    }
}
