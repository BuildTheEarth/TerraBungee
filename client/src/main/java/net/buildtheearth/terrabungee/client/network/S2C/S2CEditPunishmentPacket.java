package net.buildtheearth.terrabungee.client.network.S2C;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.players.PunishmentEditAction;

import java.util.UUID;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class S2CEditPunishmentPacket implements IS2CPacket {

    private final UUID staff;
    private final String id;
    private final PunishmentEditAction action;
    private final JsonObject data;

    @Override
    public String getType() {
        return Constants.editPunishmentID;
    }

    @Override
    public void getMessage(TerraBungeeClient instance, JsonObject data) {
        for(String key : this.data.keySet()) {
            data.add(key, this.data.get(key));
        }
        data.addProperty("staff", staff.toString());
        data.addProperty("id", id);
        data.addProperty("action", action.name());
    }
}
