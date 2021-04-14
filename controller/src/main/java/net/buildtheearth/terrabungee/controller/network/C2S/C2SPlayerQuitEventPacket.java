package net.buildtheearth.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.ServicePacket;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class C2SPlayerQuitEventPacket implements IC2SPacket {
    private final TBPlayer player;
    private final TerraBungeeService service;

    @Override
    public String getID() {
        return Constants.playerQuitEventID;
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
