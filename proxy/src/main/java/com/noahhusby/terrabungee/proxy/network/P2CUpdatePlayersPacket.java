/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - P2CUpdatePlayersPacket.java
 */

package com.noahhusby.terrabungee.proxy.network;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.network.IS2CPacket;
import com.noahhusby.terrabungee.api.players.TBPlayer;
import com.noahhusby.terrabungee.proxy.Constants;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxyMain;
import org.json.simple.JSONArray;

import java.util.List;

public class P2CUpdatePlayersPacket implements IS2CPacket {
    private final List<TBPlayer> players;

    public P2CUpdatePlayersPacket(List<TBPlayer> players) {
        this.players = players;
    }

    @Override
    public String getType() {
        return Constants.playerUpdatePacket;
    }

    @Override
    public void getMessage(TerraBungee terraBungee, JsonObject data) {
        JSONArray array = new JSONArray();
        for(TBPlayer player : players)
            array.add(TerraBungeeProxyMain.GSON.toJson(player));
    }
}
