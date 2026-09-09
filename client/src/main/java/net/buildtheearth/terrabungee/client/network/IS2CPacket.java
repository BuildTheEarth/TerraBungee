/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - IS2CPacket.java
 */

package net.buildtheearth.terrabungee.client.network;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;

public interface IS2CPacket {
    String getType();

    void getMessage(TerraBungeeClient instance, JsonObject data);
}
