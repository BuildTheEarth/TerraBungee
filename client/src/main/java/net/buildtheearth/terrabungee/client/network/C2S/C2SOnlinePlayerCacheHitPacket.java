/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - C2SOnlinePlayerCacheHitPacket.java
 */

package net.buildtheearth.terrabungee.client.network.C2S;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.player.OnlineCacheHitEvent;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;

import java.util.ArrayList;
import java.util.List;

public class C2SOnlinePlayerCacheHitPacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.onlinePlayerCacheHit;
    }

    @Override
    public void onMessage(TerraBungeeClient tb, JsonObject data) {
        JsonArray onlinePlayersArray = data.getAsJsonArray("players");
        List<TBPlayer> players = new ArrayList<>();
        for (JsonElement e : onlinePlayersArray) {
            players.add(TerraBungeeUtil.GSON.fromJson(e.getAsJsonObject(), TBPlayer.class));
        }

        tb.triggerEvent(l -> l.onOnlineCacheHit(new OnlineCacheHitEvent(tb, ImmutableList.copyOf(players))));
        tb.getPlayerManager().onlineCacheHit(players);
    }
}
