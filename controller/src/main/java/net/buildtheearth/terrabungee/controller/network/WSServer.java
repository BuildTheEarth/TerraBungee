package net.buildtheearth.terrabungee.controller.network;

import net.buildtheearth.terrabungee.common.services.ServiceStatus;
import net.buildtheearth.terrabungee.common.services.Service;
import net.buildtheearth.terrabungee.controller.security.SecurityManager;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
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
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Service service = conn.getAttachment();
        if (service != null) {
            service.setStatus(code == 1000 ? ServiceStatus.DISCARDED : ServiceStatus.LOST_CONNECTION);
            //TODO: Events
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        NetworkManager.getInstance().onIncomingPayload(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
    }

    @Override
    public void onStart() {
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        if (!SecurityManager.getInstance().verifyConnection(conn.getRemoteSocketAddress())) {
            throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "No authentication!");
        }
        return super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
    }
}
