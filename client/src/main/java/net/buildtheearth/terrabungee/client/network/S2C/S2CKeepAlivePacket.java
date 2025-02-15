/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - S2CKeepAlivePacket.java
 */

package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.TerraBungeeVersion;

public class S2CKeepAlivePacket implements IS2CPacket {
    @Override
    public String getType() {
        return Constants.keepAliveID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("type", instance.getServiceType().name());
        data.add("version", TerraBungeeUtil.GSON.toJsonTree(Constants.VERSION));
        data.add("intents", TerraBungeeUtil.intentsToArray(instance.getIntents()));
    }
}
