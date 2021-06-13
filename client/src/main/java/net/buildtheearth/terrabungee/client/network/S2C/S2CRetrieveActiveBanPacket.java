package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;

import java.util.UUID;

/**
 * @author Noah Husby
 */
@AllArgsConstructor
public class S2CRetrieveActiveBanPacket implements IS2CPacket {

    private final UUID uuid;

    @Override
    public String getType() {
        return Constants.retrieveActiveBanID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("uuid", uuid.toString());
    }
}
