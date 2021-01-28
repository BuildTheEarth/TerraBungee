package com.noahhusby.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.api.players.TBPlayer;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.console.TextComponent;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.players.PlayerManager;

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
            UUID uuid =  UUID.fromString(name);
            TBPlayer player = PlayerManager.getInstance().getPlayersRegistry().get(uuid);
            if(player == null) throw new IllegalArgumentException();
            response.setData(TerraBungeeUtil.GSON.toJsonTree(player).getAsJsonObject());
            return;
        } catch (IllegalArgumentException e) {
            for(TBPlayer player : PlayerManager.getInstance().getPlayersRegistry().values()) {
                if(player.getName().equalsIgnoreCase(name)) {
                    response.setData(TerraBungeeUtil.GSON.toJsonTree(player).getAsJsonObject());
                    return;
                }
            }
        }

        response.setCode(com.noahhusby.terrabungee.api.network.Response.ResponseCode.ERROR);
    }
}
