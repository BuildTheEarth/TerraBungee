package net.buildtheearth.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

@RequiredArgsConstructor
//TODO: Replace with proper caching
public class C2SKeepAlivePacket implements IC2SPacket {

    private final TerraBungeeService service;

    @Override
    public String getID() {
        return Constants.handshakeId;
    }

    @Override
    public void getMessage(JsonObject data) {
        data.addProperty("version", TerraBungee.getInstance().getVersion());
        data.addProperty("total_services", ServiceManager.getInstance().getServices().size());
        data.addProperty("total_disconnected_services", ServiceManager.getInstance().getTotalDisconnectedServices());
        data.addProperty("total_players", PlayerManager.getInstance().getTotalPlayers());
        data.addProperty("total_online_players", PlayerManager.getInstance().getTotalOnlinePlayers());
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
