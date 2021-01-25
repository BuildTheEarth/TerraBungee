package com.noahhusby.terrabungee.controller.network.P2C;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.api.players.TBPlayer;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;

import java.util.ArrayList;
import java.util.List;

public class P2CUpdatePlayersPacket implements IS2CPacket {
    @Override
    public String getID() {
        return "player_update";
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        JsonArray pa = data.getAsJsonArray("players");
        List<TBPlayer> players = new ArrayList<>();
        for(JsonElement e : pa)
            players.add(TerraBungeeUtil.GSON.fromJson(e, TBPlayer.class));


    }
}
