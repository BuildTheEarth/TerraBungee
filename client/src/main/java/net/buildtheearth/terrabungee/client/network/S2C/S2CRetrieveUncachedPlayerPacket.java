/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - S2CRetrieveUncachedPlayerPacket.java
 */

package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;

public class S2CRetrieveUncachedPlayerPacket implements IS2CPacket {
    private final String name;

    public S2CRetrieveUncachedPlayerPacket(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return Constants.retrieveUncachedPlayerID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("name", name);
    }
}
