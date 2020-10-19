

package com.noahhusby.terrabungee.api;

import com.google.common.collect.Lists;
import com.noahhusby.terrabungee.api.network.C2P.C2SInstanceUpdatePacket;
import com.noahhusby.terrabungee.api.network.IC2PPacket;
import com.noahhusby.terrabungee.api.network.IP2CPacket;
import com.noahhusby.terrabungee.api.network.P2C.P2CKeepAlivePacket;
import com.noahhusby.terrabungee.api.network.P2C.P2CServiceInitPacket;
import com.noahhusby.terrabungee.api.network.WebsocketEndpoint;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TerraBungeeNetworkManager {

    private final List<IC2PPacket> registeredControllerPackets = Lists.newArrayList();
    private WebsocketEndpoint websocket;

    protected TerraBungeeNetworkManager(String controller) {
        registerControllerPacket(new C2SInstanceUpdatePacket());

        try {
            websocket = new WebsocketEndpoint(new URI("ws://"+controller));
            websocket.addMessageHandler(new WebsocketEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    try {
                        onIncomingPayload((JSONObject) new JSONParser().parse(message));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if(websocket.userSession == null) {

                try {
                    websocket = new WebsocketEndpoint(new URI("ws://" + controller));
                    websocket.addMessageHandler(new WebsocketEndpoint.MessageHandler() {
                        public void handleMessage(String message) {
                            try {
                                onIncomingPayload((JSONObject) new JSONParser().parse(message));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            } else {
                sendPayload(new P2CKeepAlivePacket());
                sendPayload(new P2CServiceInitPacket());
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
        payload.put("id", TerraBungee.getInstance().getId());
        payload.put("data", packet.getMessage(new JSONObject()));
        websocket.sendMessage(payload.toJSONString());
    }
}
