/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - NetworkManager.java
 */

package net.buildtheearth.terrabungee.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.buildtheearth.terrabungee.client.events.EventListener;
import net.buildtheearth.terrabungee.client.events.controller.ControllerConnectedEvent;
import net.buildtheearth.terrabungee.client.events.controller.ControllerDisconnectEvent;
import net.buildtheearth.terrabungee.client.events.controller.DisconnectReason;
import net.buildtheearth.terrabungee.client.events.service.ServiceReconnectEvent;
import net.buildtheearth.terrabungee.client.network.C2S.C2SInstanceUpdatePacket;
import net.buildtheearth.terrabungee.client.network.C2S.C2SKeepAlivePacket;
import net.buildtheearth.terrabungee.client.network.C2S.C2SOnlinePlayerCacheHitPacket;
import net.buildtheearth.terrabungee.client.network.C2S.C2SPlayerJoinEventPacket;
import net.buildtheearth.terrabungee.client.network.C2S.C2SPlayerQuitEventPacket;
import net.buildtheearth.terrabungee.client.network.C2S.C2SResponsePacket;
import net.buildtheearth.terrabungee.client.network.C2S.C2SServiceMessagePacket;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.client.network.ResponseRequest;
import net.buildtheearth.terrabungee.client.network.S2C.S2CKeepAlivePacket;
import net.buildtheearth.terrabungee.client.network.WebsocketEndpoint;
import net.buildtheearth.terrabungee.client.util.Manager;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class NetworkManager extends Manager {

    private final List<IC2SPacket> registeredControllerPackets = Lists.newArrayList();
    private final Map<String, ResponseRequest> responseRequests = Maps.newHashMap();
    @Getter(AccessLevel.PROTECTED)
    private final List<EventListener> listeners = Lists.newArrayList();
    private String controller;
    private WebsocketEndpoint websocket;

    private boolean autoReconnect = false;
    private boolean consideredConnected = false;

    protected NetworkManager(@NonNull String controller, @NonNull TerraBungeeClient terraBungee) {
        super(terraBungee);
        this.controller = controller;

        register(new C2SKeepAlivePacket());
        register(new C2SInstanceUpdatePacket());
        register(new C2SServiceMessagePacket());
        register(new C2SResponsePacket());
        register(new C2SOnlinePlayerCacheHitPacket());
        register(new C2SPlayerJoinEventPacket());
        register(new C2SPlayerQuitEventPacket());

        TerraBungeeUtil.newSingleThreadScheduledExecutor("terrabungee-network-handler").scheduleAtFixedRate(() -> {
            if (websocket == null) {
                connect();
                return;
            }
            if (!websocket.isOnline()) {
                if (autoReconnect && consideredConnected) {
                    connect();
                }
                if (websocket.isOnline()) {
                    terraBungee.getListeners().forEach(l -> l.onServiceReconnect(new ServiceReconnectEvent(terraBungee)));
                }
            } else {
                if (!terraBungee.isDiscarded()) {
                    send(new S2CKeepAlivePacket());
                }
            }
        }, 0, 2, TimeUnit.SECONDS);

        TerraBungeeUtil.newSingleThreadScheduledExecutor("terrabungee-response-checker").scheduleAtFixedRate(this::checkResponsePacket, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Attempts the connection process to the controller
     */
    protected synchronized void connect() {
        if (websocket != null) {
            // Check the current state before attempting to reconnect
            ReadyState state = websocket.getReadyState();

            if (state == ReadyState.OPEN || state == ReadyState.NOT_YET_CONNECTED) {
                return; // Already connected or in progress, no need to reconnect
            }

            if (state == ReadyState.CLOSING || state == ReadyState.CLOSED) {
                websocket.close(); // Ensure previous connection is properly closed
            }
        }

        try {
            websocket = new WebsocketEndpoint(new URI("ws://" + controller));
            websocket.connect();
            websocket.onMessageEvent(message -> onIncomingPayload(new JsonParser().parse(message).getAsJsonObject()));
        } catch (URISyntaxException ignored) {
        }

        consideredConnected = true;

        if (websocket != null) {
            tb.triggerEvent(l -> l.onControllerConnect(new ControllerConnectedEvent(tb)));
        }
    }

    /**
     * Disconnects the service from the controller
     */
    protected void disconnect() {
        consideredConnected = false;
        try {
            websocket.close();
        } catch (Exception ignored) {
        }
        websocket = null;
        tb.getListeners().forEach(l -> l.onControllerDisconnect(new ControllerDisconnectEvent(tb, DisconnectReason.SERVICE_REQUEST)));
    }

    /**
     * Sets whether the API should automatically reconnect to the controller after loosing connection
     *
     * @param reconnect True if the API should automatically reconnect, false if not
     */
    protected void setAutoReconnect(boolean reconnect) {
        this.autoReconnect = reconnect;
    }

    /**
     * Register incoming packets
     *
     * @param packet {@link IC2SPacket}
     */
    public void register(IC2SPacket packet) {
        registeredControllerPackets.add(packet);
    }

    /**
     * Will be executed upon incoming payload for Redis/Websocket
     *
     * @param payload payload data
     */
    private void onIncomingPayload(JsonObject payload) {
        String id = payload.get("type").getAsString();

        JsonObject data = payload.getAsJsonObject("data");

        for (IC2SPacket p : registeredControllerPackets) {
            if (p.getType().equalsIgnoreCase(id)) {
                p.onMessage(tb, data);
            }
        }
    }

    /**
     * Sends a packet to the controller
     *
     * @param packet {@link IS2CPacket}
     * @return {@link Response}
     */
    public CompletableFuture<Response> send(IS2CPacket packet) {
        return send(packet, Constants.responseTimeout);
    }

    /**
     * Sends a packet to the controller
     *
     * @param packet {@link IS2CPacket}
     * @return {@link Response}
     */
    public CompletableFuture<Response> send(IS2CPacket packet, int timeout) {
        try {
            CompletableFuture<Response> future = new CompletableFuture<>();
            String salt = TerraBungeeUtil.getSaltString();
            responseRequests.put(salt, new ResponseRequest(future, timeout));

            if (!websocket.isOnline()) {
                return future;
            }

            JsonObject payload = new JsonObject();
            payload.addProperty("type", packet.getType());
            payload.addProperty("id", tb.getId());
            payload.addProperty("salt", salt);

            JsonObject data = new JsonObject();
            packet.getMessage(tb, data);
            payload.add("data", data);
            websocket.send(TerraBungeeUtil.GSON.toJson(payload));

            return future;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Checks if websocket connecting is established
     *
     * @return True for online, false for offline
     */
    public boolean isConnectionEstablished() {
        return websocket.isOnline();
    }

    /**
     * Gets a map of all response requests
     *
     * @return {@link ResponseRequest}
     */
    public Map<String, ResponseRequest> getResponseRequests() {
        return responseRequests;
    }

    /**
     * Checks if responses are expired
     */
    private void checkResponsePacket() {
        List<String> removeSalts = new ArrayList<>();

        for (Map.Entry<String, ResponseRequest> e : responseRequests.entrySet()) {
            if (e.getValue().getTime() > e.getValue().getTimeout()) {
                e.getValue().getFuture().complete(new Response(Response.ResponseCode.TIMED_OUT, new JsonObject()));
                removeSalts.add(e.getKey());
            }
        }

        for (String s : removeSalts) {
            responseRequests.remove(s);
        }
    }

    /**
     * Adds listener to TerraBungee
     *
     * @param eventListener {@link EventListener}
     */
    protected void addListener(EventListener eventListener) {
        listeners.add(eventListener);
    }

    /**
     * Removes listener from TerraBungee
     *
     * @param eventListener {@link EventListener}
     */
    protected void removeListener(EventListener eventListener) {
        listeners.remove(eventListener);
    }
}