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

import com.noahhusby.terrabungee.controller.network.S2C.S2CKeepAlivePacket;
import com.noahhusby.terrabungee.controller.network.S2C.S2CServiceInitPacket;
import io.javalin.websocket.WsContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class TerraBungeeNetworkManager {
    private static TerraBungeeNetworkManager instance;

    public static TerraBungeeNetworkManager getInstance() {
        if(instance == null) instance = new TerraBungeeNetworkManager();
        return instance;
    }

    private final List<IS2CPacket> registeredServicePackets = new ArrayList<>();

    private TerraBungeeNetworkManager() {
        registerServicePacket(new S2CServiceInitPacket());
        registerServicePacket(new S2CKeepAlivePacket());
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
        try {
            JSONObject payload = (JSONObject) new JSONParser().parse(p);
            String id = (String) payload.get("id");
            String type = (String) payload.get("type");

            ServicePacket sp = new ServicePacket(client, id);
            JSONObject data = (JSONObject) payload.get("data");

            for(IS2CPacket s : registeredServicePackets) {
                if(s.getID().equalsIgnoreCase(type)) s.onMessage(sp, data);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void sendPayload(IC2SPacket packet) {
        ServicePacket servicePacket = packet.getServicePacket();
        JSONObject payload = new JSONObject();
        payload.put("type", packet.getID());
        payload.put("id", servicePacket.getID());
        payload.put("data", packet.getMessage(new JSONObject()));

        servicePacket.getClient().send(payload.toJSONString());
    }


}
