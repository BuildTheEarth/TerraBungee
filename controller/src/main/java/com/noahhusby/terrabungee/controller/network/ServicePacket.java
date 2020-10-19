package com.noahhusby.terrabungee.controller.network;

import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import io.javalin.websocket.WsContext;

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

    public static ServicePacket fromService(ITerraBungeeService service) {
        return new ServicePacket((WsContext) service.getClient(), service.getId());
    }
}
