/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - S2CAddStaticInstancePacket.java
 */

package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;

public class S2CAddStaticInstancePacket implements IS2CPacket {

    private final String id;
    private final String address;

    public S2CAddStaticInstancePacket(String id, String address) {
        this.id = id;
        this.address = address;
    }

    @Override
    public String getType() {
        return Constants.addStaticInstanceID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("id", id);
        data.addProperty("address", address);
    }
}
