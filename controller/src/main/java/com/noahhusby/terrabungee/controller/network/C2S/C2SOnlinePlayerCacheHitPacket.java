package com.noahhusby.terrabungee.controller.network.C2S;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;

/**
 * @author Noah Husby
 */
public class C2SOnlinePlayerCacheHitPacket implements IC2SPacket {

    private final ITerraBungeeService service;

    public C2SOnlinePlayerCacheHitPacket(ITerraBungeeService service) {
        this.service = service;
    }

    @Override
    public String getID() {
        return Constants.onlinePlayerCacheHit;
    }

    @Override
    public void getMessage(JsonObject data) {
        JsonArray onlinePlayersArray = new JsonArray();

    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
