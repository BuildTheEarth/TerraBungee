package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.players.PunishmentEditAction;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class S2CEditPunishmentPacket implements IS2CPacket {

    private final String id;
    private final PunishmentEditAction action;
    private final JsonObject data;

    @Override
    public String getType() {
        return Constants.editPunishmentID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        data = this.data;
        data.addProperty("id", id);
        data.addProperty("action", action.name());
    }
}
