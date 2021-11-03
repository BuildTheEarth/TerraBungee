package net.buildtheearth.api.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import org.java_websocket.WebSocket;

@RequiredArgsConstructor
@Getter
public class ServicePacket {
    private final WebSocket client;
    private final String id;

    public static ServicePacket fromService(TerraBungeeService service) {
        return new ServicePacket((WebSocket) service.getClient(), service.getId());
    }
}
