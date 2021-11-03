package net.buildtheearth.terrabungee.controller.network.proxy;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

import java.util.List;
import java.util.UUID;

public class P2CUpdatePlayersPacket implements IS2CPacket {
    @Override
    public String getID() {
        return "player_update";
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        JsonArray pa = data.getAsJsonArray("players");
        List<ControllerPlayer> players = Lists.newArrayList();
        for (JsonElement p : pa) {
            JsonObject pl = p.getAsJsonObject();
            ControllerPlayer player = new ControllerPlayer(UUID.fromString(pl.get("uuid").getAsString()));
            player.setServer(pl.get("server").getAsString());
            player.setName(pl.get("name").getAsString());
            player.setProxy(servicePacket.getId());
            player.setOnline(true);
            players.add(player);
        }

        PlayerManager.getInstance().proxyPlayerDrop(servicePacket.getId(), players);
    }
}
