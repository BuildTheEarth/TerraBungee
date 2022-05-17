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

    @Getter
    private boolean online = false;
    private Consumer<String> messageHandler;
    private Consumer<ServerHandshake> openHandler;

    public WebsocketEndpoint(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        online = true;
        if (openHandler != null) {
            openHandler.accept(handshake);
        }
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

    public void onOpenEvent(Consumer<ServerHandshake> handshake) {
        openHandler = handshake;
    }
}