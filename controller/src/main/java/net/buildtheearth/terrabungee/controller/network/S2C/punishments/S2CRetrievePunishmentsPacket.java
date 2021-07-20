package net.buildtheearth.terrabungee.controller.network.S2C.punishments;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

import java.util.List;
import java.util.UUID;

/**
 * @author Noah Husby
 */
public class S2CRetrievePunishmentsPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.retrievePunishmentsID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        UUID uuid = UUID.fromString(data.get("uuid").getAsString());
        List<Punishment> punishments = PlayerManager.getInstance().getPunishmentsByPlayer(uuid);
        if (punishments == null) {
            response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
            return;
        }
        JsonObject responseData = new JsonObject();
        responseData.add("punishments", TerraBungeeUtil.GSON.toJsonTree(punishments).getAsJsonArray());
        response.setData(responseData);
        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
    }
}
