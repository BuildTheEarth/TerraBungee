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

import com.noahhusby.terrabungee.controller.network.C2S.C2SResponsePacket;
import com.noahhusby.terrabungee.controller.network.S2C.S2CAddStaticInstancePacket;
import com.noahhusby.terrabungee.controller.network.S2C.S2CKeepAlivePacket;
import com.noahhusby.terrabungee.controller.network.S2C.S2CRemoveStaticInstancePacket;
import com.noahhusby.terrabungee.controller.network.S2C.S2CResponsePacket;
import io.javalin.websocket.WsContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        registerServicePacket(new S2CResponsePacket());
        registerServicePacket(new S2CAddStaticInstancePacket());
        registerServicePacket(new S2CRemoveStaticInstancePacket());
    }

    private void registerServicePacket(IS2CPacket packet) {
        registeredServicePackets.add(packet);
    }

    public void onIncomingPayload(WsContext client, String p) {
        onIncomingPayload(client, p, null);
    }

    /**
     * Will be executed upon incoming payload for Redis/Websocket
     * @param client Websocket client
     * @param p Raw string data
     */
    public void onIncomingPayload(WsContext client, String p, String salt) {
        try {
            JSONObject payload = (JSONObject) new JSONParser().parse(p);
            String id = (String) payload.get("id");
            String type = (String) payload.get("type");

            ServicePacket sp = new ServicePacket(client, id);
            JSONObject data = (JSONObject) payload.get("data");

            for(IS2CPacket s : registeredServicePackets) {
                if(s.getID().equalsIgnoreCase(type)) s.onMessage(sp, data, new Response(sp, salt));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void send(IC2SPacket packet) {
        ServicePacket servicePacket = packet.getServicePacket();
        JSONObject payload = new JSONObject();
        payload.put("type", packet.getID());
        payload.put("id", servicePacket.getID());
        payload.put("data", packet.getMessage(new JSONObject()));

        servicePacket.getClient().send(payload.toJSONString());
    }

    public void respond(Response response) {
        if(response.salt == null) return;
        send(new C2SResponsePacket(response));
    }

}
