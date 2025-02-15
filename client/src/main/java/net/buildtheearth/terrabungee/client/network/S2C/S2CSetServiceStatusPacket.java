/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - S2CSetServiceStatusPacket.java
 */

package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.services.ServiceStatus;

public class S2CSetServiceStatusPacket implements IS2CPacket {

    private final String id;
    private final ServiceStatus status;

    public S2CSetServiceStatusPacket(String id, ServiceStatus status) {
        this.id = id;
        this.status = status;
    }

    @Override
    public String getType() {
        return Constants.setServiceStatusID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("id", id);
        data.addProperty("status", status.getValue());
    }
}
