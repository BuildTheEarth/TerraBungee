/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - C2SOnlinePlayerCacheHitPacket.java
 */

package net.buildtheearth.terrabungee.client.network.C2S;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.player.PlayerJoinEvent;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;

public class C2SPlayerJoinEventPacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.playerJoinEventID;
    }

    @Override
    public void onMessage(TerraBungeeClient tb, JsonObject data) {
        TBPlayer player = TerraBungeeUtil.GSON.fromJson(data.get("player"), TBPlayer.class);
        tb.triggerEvent(l -> l.onPlayerJoin(new PlayerJoinEvent(tb, player)));
    }
}
