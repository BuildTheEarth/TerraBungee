/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - P2CUpdatePlayersPacket.java
 */

package com.noahhusby.terrabungee.proxy.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.network.IS2CPacket;
import com.noahhusby.terrabungee.proxy.Constants;

public class P2CUpdatePlayersPacket implements IS2CPacket {
    private final JsonArray players;

    public P2CUpdatePlayersPacket(JsonArray players) {
        this.players = players;
    }

    @Override
    public String getType() {
        return Constants.playerUpdatePacket;
    }

    @Override
    public void getMessage(TerraBungee terraBungee, JsonObject data) {
        data.add("players", players);
    }
}
