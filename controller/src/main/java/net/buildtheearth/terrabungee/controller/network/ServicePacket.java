package net.buildtheearth.terrabungee.controller.network;

import io.javalin.websocket.WsContext;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;

public class ServicePacket {
    private final WsContext client;
    private final String id;

    public ServicePacket(WsContext client, String id) {
        this.client = client;
        this.id = id;
    }

    public WsContext getClient() {
        return client;
    }

    public String getID() {
        return id;
    }

    public static ServicePacket fromService(TerraBungeeService service) {
        return new ServicePacket((WsContext) service.getClient(), service.getId());
    }
}
