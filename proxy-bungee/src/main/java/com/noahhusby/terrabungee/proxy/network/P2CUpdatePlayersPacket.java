/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - P2CUpdatePlayersPacket.java
 */

package com.noahhusby.terrabungee.proxy.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.proxy.Constants;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;

@RequiredArgsConstructor
public class P2CUpdatePlayersPacket implements IS2CPacket {
    private final JsonArray players;

    @Override
    public String getType() {
        return Constants.playerUpdatePacket;
    }

    @Override
    public void getMessage(TerraBungeeClient terraBungee, JsonObject data) {
        data.add("players", players);
    }
}
