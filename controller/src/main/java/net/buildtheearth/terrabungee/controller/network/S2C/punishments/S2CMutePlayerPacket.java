package net.buildtheearth.terrabungee.controller.network.S2C.punishments;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Noah Husby
 */
public class S2CMutePlayerPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.mutePlayerID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        UUID playerId = UUID.fromString(data.get("playerId").getAsString());
        UUID staffId = UUID.fromString(data.get("staffId").getAsString());
        String reason = data.get("reason").getAsString();
        long length = data.get("length").getAsLong();

        List<Punishment> punishments = PlayerManager.getInstance().getPunishmentsByPlayer(playerId);
        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
        if (punishments != null) {
            for (Punishment punishment : punishments) {
                if (punishment.getType() == Punishment.Type.MUTE && punishment.isActive()) {
                    response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
                    return;
                }
            }
        }
        PlayerManager.getInstance().mute(staffId, playerId, length == 0 ? null : LocalDateTime.now().plusSeconds(length / 1000), reason);
    }
}
