package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.controller.network.IS2CPacket;
import net.buildtheearth.terrabungee.controller.network.Response;
import net.buildtheearth.terrabungee.controller.network.ServicePacket;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

import java.util.UUID;

public class S2CRetrieveUncachedPlayerPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.retrieveUncachedPlayerID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        String name = data.get("name").getAsString();
        try {
            UUID uuid = UUID.fromString(name);
            TBPlayer player = PlayerManager.getInstance().getPlayersRegistry().get(uuid);
            if (player == null) {
                throw new IllegalArgumentException();
            }
            response.setData(TerraBungeeUtil.GSON.toJsonTree(player).getAsJsonObject());
            return;
        } catch (IllegalArgumentException e) {
            for (TBPlayer player : PlayerManager.getInstance().getPlayersRegistry().values()) {
                if (player.getName().equalsIgnoreCase(name)) {
                    response.setData(TerraBungeeUtil.GSON.toJsonTree(player).getAsJsonObject());
                    return;
                }
            }
        }

        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
    }
}
