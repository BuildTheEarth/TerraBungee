/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - C2SKeepAlivePacket.java
 */

package net.buildtheearth.terrabungee.client.network.C2S;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.client.util.TBStats;
import net.buildtheearth.terrabungee.common.Constants;

public class C2SKeepAlivePacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.keepAliveID;
    }

    @Override
    public void onMessage(TerraBungeeClient instance, JsonObject data) {
        instance.setStats(new TBStats(data.get("version").getAsString(), data.get("total_services").getAsInt(),
                data.get("total_disconnected_services").getAsInt(), data.get("total_players").getAsInt(),
                data.get("total_online_players").getAsInt()));
    }
}
