package net.buildtheearth.api.network;

import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import org.java_websocket.WebSocket;

public class ServicePacket {
    private final WebSocket client;
    private final String id;

    public ServicePacket(WebSocket client, String id) {
        this.client = client;
        this.id = id;
    }

    public WebSocket getClient() {
        return client;
    }

    public String getID() {
        return id;
    }

    public static ServicePacket fromService(TerraBungeeService service) {
        return new ServicePacket((WebSocket) service.getClient(), service.getId());
    }
}
