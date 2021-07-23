package net.buildtheearth.terrabungee.controller.network;

import net.buildtheearth.terrabungee.controller.security.SecurityManager;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * @author Noah Husby
 */
public class WSServer extends WebSocketServer {

    public WSServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if(!SecurityManager.getInstance().verifyConnection(conn.getRemoteSocketAddress())) {
            conn.close();
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if(!SecurityManager.getInstance().verifyConnection(conn.getRemoteSocketAddress())) {
            conn.close();
            return;
        }
        NetworkManager.getInstance().onIncomingPayload(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {
    }
}
