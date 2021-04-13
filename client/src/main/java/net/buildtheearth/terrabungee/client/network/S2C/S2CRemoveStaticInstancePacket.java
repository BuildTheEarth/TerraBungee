/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - S2CRemoveStaticInstancePacket.java
 */

package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;

public class S2CRemoveStaticInstancePacket implements IS2CPacket {

    private final String id;

    public S2CRemoveStaticInstancePacket(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return Constants.removeStaticInstanceID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("id", id);
    }
}
