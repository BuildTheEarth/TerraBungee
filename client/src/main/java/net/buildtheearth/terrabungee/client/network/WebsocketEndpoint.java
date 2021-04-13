/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - WebsocketEndpoint.java
 */

package net.buildtheearth.terrabungee.client.network;

import lombok.Getter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class WebsocketEndpoint extends WebSocketClient {

    public WebsocketEndpoint(URI serverUri) {
        super(serverUri);
    }

    @Getter
    private boolean online = false;
    private Consumer<String> messageHandler;

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        online = true;
    }

    @Override
    public void onMessage(String message) {
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        online = false;
    }

    @Override
    public void onError(Exception ex) {
        online = false;
    }

    public void onMessageEvent(Consumer<String> message) {
        messageHandler = message;
    }
}