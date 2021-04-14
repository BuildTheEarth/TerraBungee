/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * IC2PPacket.java
 */
package net.buildtheearth.api.network;

import com.google.gson.JsonObject;

public interface IC2SPacket {
    String getID();

    void getMessage(JsonObject data);

    ServicePacket getServicePacket();
}
