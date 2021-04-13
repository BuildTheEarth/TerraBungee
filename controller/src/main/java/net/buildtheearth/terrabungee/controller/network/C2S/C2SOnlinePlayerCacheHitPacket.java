package net.buildtheearth.terrabungee.controller.network.C2S;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.network.IC2SPacket;
import net.buildtheearth.terrabungee.controller.network.ServicePacket;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

/**
 * @author Noah Husby
 */
public class C2SOnlinePlayerCacheHitPacket implements IC2SPacket {

    private final TerraBungeeService service;

    public C2SOnlinePlayerCacheHitPacket(TerraBungeeService service) {
        this.service = service;
    }

    @Override
    public String getID() {
        return Constants.onlinePlayerCacheHit;
    }

    @Override
    public void getMessage(JsonObject data) {
        JsonArray onlinePlayersArray = new JsonArray();
        for (TBPlayer p : PlayerManager.getInstance().getOnlinePlayerRegistry().values()) {
            onlinePlayersArray.add(TerraBungeeUtil.GSON.toJsonTree(p));
        }

        data.add("players", onlinePlayersArray);
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
