package net.buildtheearth.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.network.IC2SPacket;
import net.buildtheearth.terrabungee.controller.network.ServicePacket;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class C2SPlayerJoinEventPacket implements IC2SPacket {
    private final TBPlayer player;
    private final TerraBungeeService service;

    @Override
    public String getID() {
        return Constants.playerJoinEventID;
    }

    @Override
    public void getMessage(JsonObject data) {
        data.add("player", TerraBungeeUtil.GSON.toJsonTree(player));
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
