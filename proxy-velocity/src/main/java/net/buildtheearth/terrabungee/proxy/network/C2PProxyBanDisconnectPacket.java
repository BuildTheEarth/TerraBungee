package net.buildtheearth.terrabungee.proxy.network;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.players.PlayerHandler;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;

import java.util.Optional;

/**
 * @author Noah Husby & XboxBedrock
 */
public class C2PProxyBanDisconnectPacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.proxyBanDisconnectID;
    }

    @Override
    public void onMessage(TerraBungeeClient instance, JsonObject data) {
        Punishment punishment = TerraBungeeUtil.GSON.fromJson(data.get("punishment"), Punishment.class);
        Optional<Player> player = TerraBungeeProxy.getServer().getPlayer(punishment.getPlayer());
        if (player.isEmpty()) {
            return;
        }
        player.get().disconnect(PlayerHandler.getInstance().getBanDisconnectMessage(punishment));
    }
}
