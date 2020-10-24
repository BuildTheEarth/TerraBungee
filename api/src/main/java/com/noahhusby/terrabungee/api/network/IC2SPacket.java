/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * IC2PPacket.java
 */
package com.noahhusby.terrabungee.api.network;

import com.noahhusby.terrabungee.api.TerraBungee;
import org.json.simple.JSONObject;

public interface IC2SPacket {
    String getType();
    void onMessage(TerraBungee instance, JSONObject data);
}
