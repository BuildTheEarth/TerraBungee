package net.buildtheearth.terrabungee.controller.network;

import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.security.SecurityManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;
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
        ServiceManager.getInstance().markDisconnected(conn);
        TerraBungeeController.logger.warning("TerraBungee connection closed (" + code + ", remote="
                + remote + ") from " + remoteAddress(conn) + ": " + reason);
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
        ServiceManager.getInstance().markDisconnected(conn);
        TerraBungeeController.logger.warning("TerraBungee websocket error from " + remoteAddress(conn)
                + ": " + (ex == null ? "unknown error" : ex.getMessage()));
    }

    @Override
    public void onStart() {
    }

    private String remoteAddress(WebSocket conn) {
        return conn == null || conn.getRemoteSocketAddress() == null
                ? "unknown"
                : conn.getRemoteSocketAddress().toString();
    }
}
