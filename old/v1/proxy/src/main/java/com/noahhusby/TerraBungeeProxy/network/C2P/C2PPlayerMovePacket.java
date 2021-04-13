/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * C2PPlayerMovePacket.java
 */

package com.noahhusby.TerraBungeeProxy.network.C2P;

import com.noahhusby.TerraBungeeProxy.Constants;
import com.noahhusby.TerraBungeeProxy.TerraBungeeProxyMain;
import com.noahhusby.TerraBungeeProxy.network.IC2PPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.simple.JSONObject;

public class C2PPlayerMovePacket implements IC2PPacket {

    @Override
    public String getType() {
        return Constants.playerMoveID;
    }

    @Override
    public void onMessage(JSONObject data) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer((String) data.get("player"));
        if(player == null) {
            TerraBungeeProxyMain.getInstance().getLogger().warning("The specified player in the player move packet does not exist or is not online.");
            return;
        }

        ServerInfo server = ProxyServer.getInstance().getServerInfo((String) data.get("server"));
        if(server == null) {
            TerraBungeeProxyMain.getInstance().getLogger().warning("The specified server in the player move packet does not exist or is not online.");
            return;
        }

        player.connect(server);
    }
}
