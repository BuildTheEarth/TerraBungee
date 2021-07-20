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
public class S2CRetrieveActiveBanPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.retrieveActiveBanID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        UUID player = UUID.fromString(data.get("uuid").getAsString());
        List<Punishment> punishments = PlayerManager.getInstance().getPunishmentsByPlayer(player);
        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
        response.setData(new JsonObject());
        if (punishments == null || punishments.isEmpty()) {
            return;
        }
        for (Punishment punishment : punishments) {
            if (punishment.getType() == Punishment.Type.BAN && punishment.isActive()) {
                response.setData(TerraBungeeUtil.GSON.toJsonTree(punishment).getAsJsonObject());
                response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
                return;
            }
        }
    }
}
