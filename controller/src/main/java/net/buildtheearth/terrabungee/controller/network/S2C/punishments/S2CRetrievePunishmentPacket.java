package net.buildtheearth.terrabungee.controller.network.S2C.punishments;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

/**
 * @author Noah Husby
 */
public class S2CRetrievePunishmentPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.retrievePunishmentID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        String idString = data.get("id").getAsString();
        int id = Integer.parseInt(idString);
        Punishment punishment = PlayerManager.getInstance().getPunishments().get(id);
        if (punishment == null) {
            response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
        } else {
            response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
            JsonObject responseData = new JsonObject();
            responseData.add("punishment", TerraBungeeUtil.GSON.toJsonTree(punishment).getAsJsonObject());
            responseData.addProperty("playerName", PlayerManager.getInstance().getPlayers().get(punishment.getPlayer()).getName());
            TBPlayer staff = PlayerManager.getInstance().getPlayers().get(punishment.getStaff());
            responseData.addProperty("staffName", staff == null ? "Console" : staff.getName());
            response.setData(responseData);
        }
    }
}
