/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - S2CServiceMessagePacket.java
 */

package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;

public class S2CServiceMessagePacket implements IS2CPacket {

    private final String id;
    private final JsonObject message;

    public S2CServiceMessagePacket(String id, JsonObject message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public String getType() {
        return Constants.serviceMessageID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("message", message.toString());
        data.addProperty("to", id);
    }
}
