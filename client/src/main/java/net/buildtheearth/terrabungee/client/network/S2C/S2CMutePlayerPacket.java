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
public class S2CMutePlayerPacket implements IS2CPacket {
    private final UUID staff;
    private final UUID player;
    private final long length;
    private final String reason;

    @Override
    public String getType() {
        return Constants.mutePlayerID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data.addProperty("staffId", staff.toString());
        data.addProperty("playerId", player.toString());
        data.addProperty("length", length);
        data.addProperty("reason", reason);
    }
}
