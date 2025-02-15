/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - S2CRetrieveUncachedPlayerPacket.java
 */

package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;

public class S2CRetrieveUncachedPlayersPacket implements IS2CPacket {
    private final boolean all;

    public S2CRetrieveUncachedPlayersPacket(boolean all) {
        this.all = all;
    }

    @Override
    public String getType() {
        return Constants.retrieveUncachedPlayersID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("all", all);
    }
}
