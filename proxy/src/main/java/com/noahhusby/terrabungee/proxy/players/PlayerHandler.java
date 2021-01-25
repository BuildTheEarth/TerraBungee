/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - PlayerHandler.java
 */

package com.noahhusby.terrabungee.proxy.players;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.network.P2CUpdatePlayersPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;

public class PlayerHandler {
    private static PlayerHandler instance = null;
    public static PlayerHandler getInstance() {
        return instance == null ? instance = new PlayerHandler() : instance;
    }

    private PlayerHandler() {
        TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();
        scheduler.schedule(TerraBungeeProxy.getInstance(), () -> scheduler.runAsync(TerraBungeeProxy.getInstance(), () -> {
            if(TerraBungeeProxy.tb == null) return;
            JsonArray array = new JsonArray();
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                JsonObject player = new JsonObject();
                player.addProperty("uuid", p.getUniqueId().toString());
                player.addProperty("name", p.getName());
                player.addProperty("server", p.getServer().getInfo().getName());
                array.add(player);
            }

            TerraBungeeProxy.tb.getNetworkManager().send(new P2CUpdatePlayersPacket(array));
        }), 0, 2, TimeUnit.SECONDS);
    }
}
