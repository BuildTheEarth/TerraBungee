

package com.noahhusby.terrabungee.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.noahhusby.terrabungee.api.network.*;
import com.noahhusby.terrabungee.api.network.C2P.C2SInstanceUpdatePacket;
import com.noahhusby.terrabungee.api.network.C2P.C2SResponsePacket;
import com.noahhusby.terrabungee.api.network.S2C.S2CKeepAlivePacket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NetworkManager {

    private final List<IC2SPacket> registeredControllerPackets = Lists.newArrayList();
    private final List<Response> responsePackets = Lists.newArrayList();
    private final TerraBungee terraBungee;
    private WebsocketEndpoint websocket;

    protected NetworkManager(String controller, TerraBungee terraBungee) {
        this.terraBungee = terraBungee;
        register(new C2SInstanceUpdatePacket());
        register(new C2SResponsePacket());

        try {
            websocket = new WebsocketEndpoint(new URI("ws://"+controller));
            websocket.addMessageHandler(message -> {
                try {
                    onIncomingPayload((JSONObject) new JSONParser().parse(message));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        terraBungee.getExecutorService().scheduleAtFixedRate(() -> {
            if(websocket.userSession == null) {

                try {
                    websocket = new WebsocketEndpoint(new URI("ws://" + controller));
                    websocket.addMessageHandler(message -> {
                        try {
                            onIncomingPayload((JSONObject) new JSONParser().parse(message));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            } else {
                send(new S2CKeepAlivePacket());
            }
        }, 0, 2, TimeUnit.SECONDS);

        terraBungee.getExecutorService().scheduleAtFixedRate(this::checkResponsePacket, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void register(IC2SPacket packet) {
        registeredControllerPackets.add(packet);
    }

    /**
     * Will be executed upon incoming payload for Redis/Websocket
     * @param payload payload data
     */
    private void onIncomingPayload(JSONObject payload) {
        System.out.println(payload.toJSONString());
        String id = (String) payload.get("type");
        JSONObject data = (JSONObject) payload.get("data");

        for(IC2SPacket p : registeredControllerPackets) {
            if(p.getType().equalsIgnoreCase(id)) p.onMessage(terraBungee, data);
        }
    }

    /**
     * Sends a packet to the controller
     * @param packet The packet to be sent
     */
    public void send(IS2CPacket packet) {
        if(websocket.userSession == null) return;
        JSONObject payload = new JSONObject();
        payload.put("type", packet.getType());
        payload.put("id", terraBungee.getId());
        payload.put("data", packet.getMessage(terraBungee, new JSONObject()));
        websocket.sendMessage(payload.toJSONString());
    }

    /**
     * Registers a response for a packet
     * @param response The response
     */
    public void addResponse(Response response) {
        responsePackets.add(response);
    }

    /**
     * Called when the controller has a response for a given packet. This should not be called from services.
     * @param salt Unique Salt Code
     * @param code Response Code from Controller
     * @param data Data from Controller
     */
    public void onResponsePacket(String salt, Response.ResponseCode code, JSONObject data) {
        Response remove = null;

        for(Response r : responsePackets) {
            if(r.salt.equals(salt)) {
                remove = r;
                r.response.onResponse(code, data);
            }
        }

        if(remove != null) responsePackets.remove(remove);
    }

    /**
     * Checks if responses are expired
     */
    private void checkResponsePacket() {
        List<Response> removableResponses = new ArrayList<>();

        for(Response r : responsePackets) {
            if(r.time + r.timeout < System.currentTimeMillis()) {
                r.response.onResponse(Response.ResponseCode.TIMED_OUT, new JSONObject());
                removableResponses.add(r);
            }
        }

        for(Response r : removableResponses)
            responsePackets.remove(r);
    }
}
