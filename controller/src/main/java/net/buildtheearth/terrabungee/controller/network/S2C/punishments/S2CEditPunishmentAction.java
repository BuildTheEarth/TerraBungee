package net.buildtheearth.terrabungee.controller.network.S2C.punishments;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.players.PunishmentEditAction;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

/**
 * @author Noah Husby
 */
public class S2CEditPunishmentAction implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.editPunishmentID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
        int id = Integer.parseInt(data.get("id").getAsString());
        PunishmentEditAction action = PunishmentEditAction.valueOf(data.get("action").getAsString());
        PlayerManager.getInstance().editPunishment(id, action, data);
    }
}
