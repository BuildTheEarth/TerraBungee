/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - PlayerHandler.java
 */

package com.noahhusby.terrabungee.proxy.players;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noahhusby.terrabungee.api.players.TBPlayer;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxyMain;
import com.noahhusby.terrabungee.proxy.network.P2CUpdatePlayersPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerHandler {
    private static PlayerHandler instance = null;
    public static PlayerHandler getInstance() {
        return instance == null ? instance = new PlayerHandler() : instance;
    }

    private PlayerHandler() {
        TerraBungeeProxyMain.threads.scheduleAtFixedRate(() -> {
            List<TBPlayer> players = new ArrayList<>();
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                TBPlayer player = new TBPlayer(p.getUniqueId());
                player.setOnline(true);
                player.setName(p.getName());
                player.setServer(p.getServer().getInfo().getName());
                players.add(player);
            }

            TerraBungeeProxyMain.tb.getNetworkManager().send(new P2CUpdatePlayersPacket(players));
        }, 0, 2, TimeUnit.SECONDS);
    }

}
