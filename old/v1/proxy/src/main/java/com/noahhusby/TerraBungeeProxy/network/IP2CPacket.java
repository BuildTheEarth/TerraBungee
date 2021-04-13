/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * IP2CPacket.java
 */

package com.noahhusby.TerraBungeeProxy.network;

import org.json.simple.JSONObject;

public interface IP2CPacket {
    String getType();
    JSONObject getMessage(JSONObject data);
}
