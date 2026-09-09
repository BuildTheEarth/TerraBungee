/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - NetworkManager.java
 */

package net.buildtheearth.terrabungee.client;

import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class NetworkManager extends Manager {

    private final List<IC2SPacket> registeredControllerPackets = Lists.newArrayList();
    private final Map<String, ResponseRequest> responseRequests = new ConcurrentHashMap<>();
    @Getter(AccessLevel.PROTECTED)
    private final List<EventListener> listeners = Lists.newArrayList();
    private final String controller;
    private volatile WebsocketEndpoint websocket;

    private volatile boolean autoReconnect = false;
    private volatile boolean connectionRequested = false;
    private volatile boolean connectionObserved = false;
    private volatile boolean everConnected = false;
    private volatile long connectionAttemptStarted = 0;

    private static final long CONNECTION_ATTEMPT_TIMEOUT_MS = 10_000;

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

        TerraBungeeUtil.newSingleThreadScheduledExecutor("terrabungee-network-handler")
                .scheduleAtFixedRate(this::checkConnection, 0, 2, TimeUnit.SECONDS);

        TerraBungeeUtil.newSingleThreadScheduledExecutor("terrabungee-response-checker").scheduleAtFixedRate(this::checkResponsePacket, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Attempts the connection process to the controller
     */
    protected synchronized void connect() {
        connectionRequested = true;

        WebsocketEndpoint current = websocket;
        if (current != null) {
            ReadyState state = current.getReadyState();
            if (current.isOnline() && state == ReadyState.OPEN) {
                return;
            }
            if (state == ReadyState.NOT_YET_CONNECTED
                    && System.currentTimeMillis() - connectionAttemptStarted < CONNECTION_ATTEMPT_TIMEOUT_MS) {
                return;
            }
            closeEndpoint(current);
            if (websocket == current) {
                websocket = null;
            }
        }

        try {
            WebsocketEndpoint endpoint = new WebsocketEndpoint(new URI("ws://" + controller));
            endpoint.onMessageEvent(message -> {
                try {
                    onIncomingPayload(new JsonParser().parse(message).getAsJsonObject());
                } catch (Exception e) {
                    tb.getLogger().warning("Unable to process a TerraBungee controller payload: " + e.getMessage());
                }
            });
            websocket = endpoint;
            connectionAttemptStarted = System.currentTimeMillis();
            endpoint.connect();
        } catch (URISyntaxException e) {
            tb.getLogger().warning("Invalid TerraBungee controller address: " + controller);
        } catch (Exception e) {
            tb.getLogger().warning("Unable to connect to the TerraBungee controller: " + e.getMessage());
        }
    }

    /**
     * Disconnects the service from the controller
     */
    protected synchronized void disconnect() {
        connectionRequested = false;
        WebsocketEndpoint current = websocket;
        websocket = null;
        connectionObserved = false;
        closeEndpoint(current);
        tb.triggerEvent(l -> l.onControllerDisconnect(
                new ControllerDisconnectEvent(tb, DisconnectReason.SERVICE_REQUEST)));
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
        CompletableFuture<Response> future = new CompletableFuture<>();
        WebsocketEndpoint current = websocket;
        if (current == null || !current.isOnline() || current.getReadyState() != ReadyState.OPEN) {
            future.complete(errorResponse());
            return future;
        }

        String salt = TerraBungeeUtil.getSaltString();
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("type", packet.getType());
            payload.addProperty("id", tb.getId());
            payload.addProperty("salt", salt);

            JsonObject data = new JsonObject();
            packet.getMessage(tb, data);
            payload.add("data", data);

            responseRequests.put(salt, new ResponseRequest(future, timeout));
            current.send(TerraBungeeUtil.GSON.toJson(payload));

            return future;
        } catch (Exception e) {
            responseRequests.remove(salt);
            future.complete(errorResponse());
            tb.getLogger().warning("Unable to send a TerraBungee packet: " + e.getMessage());
        }

        return future;
    }

    /**
     * Checks if websocket connecting is established
     *
     * @return True for online, false for offline
     */
    public boolean isConnectionEstablished() {
        WebsocketEndpoint current = websocket;
        return current != null && current.isOnline() && current.getReadyState() == ReadyState.OPEN;
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
        responseRequests.forEach((salt, request) -> {
            if (request.getTime() > request.getTimeout() && responseRequests.remove(salt, request)) {
                request.getFuture().complete(new Response(Response.ResponseCode.TIMED_OUT, new JsonObject()));
            }
        });
    }

    private void checkConnection() {
        boolean online = isConnectionEstablished();
        if (online) {
            if (!connectionObserved) {
                connectionObserved = true;
                if (everConnected) {
                    tb.triggerEvent(l -> l.onServiceReconnect(new ServiceReconnectEvent(tb)));
                } else {
                    everConnected = true;
                    tb.triggerEvent(l -> l.onControllerConnect(new ControllerConnectedEvent(tb)));
                }
            }
            if (!tb.isDiscarded()) {
                send(new S2CKeepAlivePacket());
            }
            return;
        }

        if (connectionObserved) {
            connectionObserved = false;
            tb.triggerEvent(l -> l.onControllerDisconnect(
                    new ControllerDisconnectEvent(tb, DisconnectReason.LOST_CONNECTION)));
        }

        if (connectionRequested && autoReconnect) {
            connect();
        }
    }

    private void closeEndpoint(WebsocketEndpoint endpoint) {
        if (endpoint == null) {
            return;
        }
        try {
            endpoint.close();
        } catch (Exception ignored) {
        }
    }

    private Response errorResponse() {
        return new Response(Response.ResponseCode.ERROR, new JsonObject());
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
