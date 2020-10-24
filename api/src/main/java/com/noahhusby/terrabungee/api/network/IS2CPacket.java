/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * IP2CPacket.java
 */

package com.noahhusby.terrabungee.api.network;

import com.noahhusby.terrabungee.api.TerraBungee;
import org.json.simple.JSONObject;

public interface IS2CPacket {
    String getType();
    JSONObject getMessage(TerraBungee instance, JSONObject data);
}
