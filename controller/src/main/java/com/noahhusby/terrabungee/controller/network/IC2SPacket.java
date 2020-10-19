/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * IC2PPacket.java
 */
package com.noahhusby.terrabungee.controller.network;

import org.json.simple.JSONObject;

public interface IC2SPacket {
    String getID();
    JSONObject getMessage(JSONObject data);
    ServicePacket getServicePacket();
}
