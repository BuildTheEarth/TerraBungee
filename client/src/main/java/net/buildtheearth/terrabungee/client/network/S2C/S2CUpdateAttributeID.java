/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - S2CUpdateAttributeID.java
 */

package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;

public class S2CUpdateAttributeID implements IS2CPacket {

    private final TBPlayer player;

    public S2CUpdateAttributeID(TBPlayer player) {
        this.player = player;
    }

    @Override
    public String getType() {
        return Constants.updateAttributeID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("uuid", player.getUniqueID().toString());
        data.add("attributes", TerraBungeeUtil.GSON.toJsonTree(player.getAttributes()));
    }
}
