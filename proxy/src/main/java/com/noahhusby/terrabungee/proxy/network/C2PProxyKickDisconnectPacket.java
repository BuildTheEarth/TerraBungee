package com.noahhusby.terrabungee.proxy.network;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.proxy.players.PlayerHandler;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Noah Husby
 */
public class C2PProxyKickDisconnectPacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.proxyKickDisconnectID;
    }

    @Override
    public void onMessage(TerraBungeeClient instance, JsonObject data) {
        Punishment punishment = TerraBungeeUtil.GSON.fromJson(data.get("punishment"), Punishment.class);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(punishment.getPlayer());
        if(player == null) {
            return;
        }
        player.disconnect(PlayerHandler.getInstance().getKickDisconnectMessage(punishment));
    }
}
