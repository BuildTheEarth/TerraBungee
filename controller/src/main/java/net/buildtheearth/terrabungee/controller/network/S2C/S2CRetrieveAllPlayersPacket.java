package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import com.noahhusby.lib.data.storage.StorageHashMap;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.controller.network.FakePacketPlayer;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

import java.util.ArrayList;
import java.util.UUID;

public class S2CRetrieveAllPlayersPacket implements  IS2CPacket {
    @Override
    public String getID() {
        return Constants.getAllPlayersID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {

        StorageHashMap<UUID, ControllerPlayer> players = PlayerManager.getInstance().getPlayers();

        ArrayList<FakePacketPlayer> playerList = new ArrayList<>();

        for (TBPlayer player : players.values()) {
            if (player.isOnline()) {
                FakePacketPlayer fakePacketPlayer = new FakePacketPlayer(player);
                playerList.add(fakePacketPlayer);
            }
        }

        response.setData(TerraBungeeUtil.GSON.toJsonTree(playerList).getAsJsonArray());
    }
}