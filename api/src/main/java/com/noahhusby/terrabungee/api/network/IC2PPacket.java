/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * IC2PPacket.java
 */
package com.noahhusby.terrabungee.api.network;

import org.json.simple.JSONObject;

public interface IC2PPacket {
    String getType();
    void onMessage(JSONObject data);
}
