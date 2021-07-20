package net.buildtheearth.terrabungee.controller.network.S2C.punishments;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

import java.util.UUID;

/**
 * @author Noah Husby
 */
public class S2CKickPlayerPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.kickPlayerID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        UUID playerId = UUID.fromString(data.get("playerId").getAsString());
        UUID staffId = UUID.fromString(data.get("staffId").getAsString());
        String reason = data.get("reason").getAsString();
        TBPlayer player = PlayerManager.getInstance().getOnlinePlayerRegistry().get(playerId);
        if (player == null || !player.isOnline()) {
            response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
            return;
        }
        PlayerManager.getInstance().kick(staffId, playerId, reason);
        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
    }
}
