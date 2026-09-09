/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - IC2SPacket.java
 */
package net.buildtheearth.terrabungee.client.network;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;

public interface IC2SPacket {
    String getType();

    void onMessage(TerraBungeeClient instance, JsonObject data);
}
