package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class S2CRetrievePunishmentPacket implements IS2CPacket {

    private final String id;

    @Override
    public String getType() {
        return Constants.retrievePunishmentID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("id", id);
    }
}
