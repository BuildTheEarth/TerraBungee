package net.buildtheearth.terrabungee.controller.network.S2C.punishments;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Noah Husby
 */
public class S2CBanPlayerPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.banPlayerID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        UUID playerId = UUID.fromString(data.get("playerId").getAsString());
        UUID staffId = UUID.fromString(data.get("staffId").getAsString());
        String reason = data.get("reason").getAsString();
        String lengthString = data.get("length").getAsString();
        int length = Integer.parseInt(lengthString);

        List<Punishment> punishments = PlayerManager.getInstance().getPunishmentsByPlayer(playerId);
        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
        if(punishments != null) {
            for(Punishment punishment : punishments) {
                if(punishment.getType() == Punishment.Type.BAN && punishment.isActive()) {
                    response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
                    return;
                }
            }
        }
        PlayerManager.getInstance().ban(staffId, playerId, length == 0 ? null : LocalDateTime.now().plusDays(length), reason);
    }
}
