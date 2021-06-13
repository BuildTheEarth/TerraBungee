/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - PlayerHandler.java
 */

package com.noahhusby.terrabungee.proxy.players;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.network.P2CUpdatePlayersPacket;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerHandler {
    private static PlayerHandler instance = null;

    public static PlayerHandler getInstance() {
        return instance == null ? instance = new PlayerHandler() : instance;
    }

    private PlayerHandler() {
        TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();
        scheduler.schedule(TerraBungeeProxy.getInstance(), () -> scheduler.runAsync(TerraBungeeProxy.getInstance(), () -> {
            if (TerraBungeeProxy.getInstance().getTerraBungee() == null || !TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().isConnectionEstablished()) {
                return;
            }
            JsonArray array = new JsonArray();
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                if (p.getServer() == null || p.getServer().getInfo() == null) {
                    continue;
                }
                JsonObject player = new JsonObject();
                player.addProperty("uuid", p.getUniqueId().toString());
                player.addProperty("name", p.getName());
                player.addProperty("server", p.getServer().getInfo().getName());
                array.add(player);
            }

            TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new P2CUpdatePlayersPacket(array));
        }), 0, 2, TimeUnit.SECONDS);
    }

    public BaseComponent getBanDisconnectMessage(Punishment punishment) {
        TextComponent kickMessage;
        if(punishment.getEnd() == null) {
            kickMessage = ChatUtil.combine(ChatColor.RED, "You are permanently banned from BuildTheEarth!\n\n");
        } else {
            long difference = punishment.getEnd().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - new Date().getTime();
            long days = TimeUnit.MILLISECONDS.toDays(difference) % 365;
            long hours = TimeUnit.MILLISECONDS.toHours(difference) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(difference) % 50;
            kickMessage = ChatUtil.combine(ChatColor.RED, "You are temporarily banned for ", ChatColor.RESET, days, "d ", hours, "h ", minutes, "m ", seconds, "s ", ChatColor.RED, "from BuildTheEarth!\n\n");
        }
        kickMessage.addExtra(ChatUtil.combine(ChatColor.GRAY, "Reason: ", ChatColor.WHITE, punishment.getReason(), "\n"));
        kickMessage.addExtra(ChatUtil.combine(ChatColor.GRAY, "Punishment ID: ", ChatColor.WHITE, "#", punishment.getId()));
        return kickMessage;
    }
}
