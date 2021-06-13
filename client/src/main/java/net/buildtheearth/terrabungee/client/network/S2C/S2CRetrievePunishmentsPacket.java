package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;

import java.util.UUID;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class S2CRetrievePunishmentsPacket implements IS2CPacket {

    private final UUID uuid;

    @Override
    public String getType() {
        return Constants.retrievePunishmentsID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("uuid", uuid.toString());
    }
}
