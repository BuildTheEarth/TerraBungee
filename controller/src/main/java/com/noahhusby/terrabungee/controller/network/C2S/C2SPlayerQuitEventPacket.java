package com.noahhusby.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.api.players.TBPlayer;
import com.noahhusby.terrabungee.api.services.TerraBungeeService;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import lombok.RequiredArgsConstructor;

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
