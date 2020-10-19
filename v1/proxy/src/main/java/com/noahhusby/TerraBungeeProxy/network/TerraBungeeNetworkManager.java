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

package com.noahhusby.TerraBungeeProxy.network;

import com.google.common.collect.Lists;
import com.noahhusby.TerraBungeeProxy.config.ConfigHandler;
import com.noahhusby.TerraBungeeProxy.network.C2P.C2PPlayerMovePacket;
import com.noahhusby.TerraBungeeProxy.network.P2C.P2CKeepAlivePacket;
import com.noahhusby.TerraBungeeProxy.network.P2C.P2CProxyInitPacket;
import org.json.simple.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TerraBungeeNetworkManager {
    private static TerraBungeeNetworkManager instance;

    public static TerraBungeeNetworkManager getInstance() {
        if(instance == null) instance = new TerraBungeeNetworkManager();
        return instance;
    }

    private final List<IC2PPacket> registeredControllerPackets = Lists.newArrayList();
    private WebsocketEndpoint websocket;

    private TerraBungeeNetworkManager() {
        registerControllerPacket(new C2PPlayerMovePacket());
        try {
            websocket = new WebsocketEndpoint(new URI("ws://127.0.0.1:7000"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(websocket.userSession == null) {
                    try {
                        websocket = new WebsocketEndpoint(new URI("ws://127.0.0.1:7000"));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    sendPayload(new P2CProxyInitPacket());
                    sendPayload(new P2CKeepAlivePacket());
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void registerControllerPacket(IC2PPacket packet) {
        registeredControllerPackets.add(packet);
    }

    /**
     * Will be executed upon incoming payload for Redis/Websocket
     * @param payload payload data
     */
    public void onIncomingPayload(JSONObject payload) {
        String id = (String) payload.get("type");
        JSONObject data = (JSONObject) payload.get("data");

        for(IC2PPacket p : registeredControllerPackets) {
            if(p.getType().equalsIgnoreCase(id)) p.onMessage(data);
        }
    }

    public void sendPayload(IP2CPacket packet) {
        JSONObject payload = new JSONObject();
        payload.put("type", packet.getType());
        payload.put("id", ConfigHandler.serviceID);
        payload.put("data", packet.getMessage(new JSONObject()));
        websocket.sendMessage(payload.toJSONString());
    }
}
