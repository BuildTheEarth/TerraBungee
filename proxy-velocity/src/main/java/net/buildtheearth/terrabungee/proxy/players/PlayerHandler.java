/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - PlayerHandler.java
 */

package net.buildtheearth.terrabungee.proxy.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.Scheduler;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.network.P2CUpdatePlayersPacket;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerHandler {
    private static PlayerHandler instance = null;

    public static PlayerHandler getInstance() {
        return instance == null ? instance = new PlayerHandler() : instance;
    }

    private PlayerHandler() {
        Scheduler scheduler = TerraBungeeProxy.getServer().getScheduler();
        // 🦀 No more main thread in velocity 🦀
        scheduler.buildTask(TerraBungeeProxy.getInstance(), () -> {
                    if (TerraBungeeProxy.getInstance().getTerraBungee() == null || !TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().isConnectionEstablished()) {
                        return;
                    }
                    JsonArray array = new JsonArray();
                    for (Player p : TerraBungeeProxy.getServer().getAllPlayers()) {
                        if (p.getCurrentServer().isEmpty() || p.getCurrentServer().get().getServerInfo() == null) {
                            continue;
                        }
                        JsonObject player = new JsonObject();
                        player.addProperty("uuid", p.getUniqueId().toString());
                        player.addProperty("name", p.getUsername());
                        player.addProperty("server", p.getCurrentServer().get().getServerInfo().getName());
                        array.add(player);
                    }

                    TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new P2CUpdatePlayersPacket(array));
                }).delay(0, TimeUnit.SECONDS)
                .repeat(2, TimeUnit.SECONDS)
                .schedule();
    }

    @Getter
    @Setter
    private Map<UUID, Punishment> muteCache = Maps.newHashMap();

    @Getter
    @Setter
    private List<String> onlinePlayerNames = Lists.newArrayList();

    public Component getBanDisconnectMessage(Punishment punishment) {
        Component kickMessage;
        if (punishment.getEnd() == null) {
            kickMessage = Component.text("You are permanently banned from BuildTheEarth!", NamedTextColor.RED)
                    .appendNewline()
                    .appendNewline();
        } else {
            long difference = LocalDateTime.parse(punishment.getEnd()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - new Date().getTime();
            long days = TimeUnit.MILLISECONDS.toDays(difference) % 365;
            long hours = TimeUnit.MILLISECONDS.toHours(difference) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(difference) % 50;
            kickMessage = Component.text("You are temporarily banned for ", NamedTextColor.RED)
                    .append(Component.text(days + "d "))
                    .append(Component.text(hours + "h "))
                    .append(Component.text(minutes + "m "))
                    .append(Component.text(seconds + "s "))
                    .append(Component.text("from BuildTheEarth!", NamedTextColor.RED))
                    .appendNewline()
                    .appendNewline();
        }
        kickMessage = kickMessage.append(Component.text("Reason: ", NamedTextColor.GRAY))
                .append(Component.text(punishment.getReason(), NamedTextColor.WHITE))
                .appendNewline()
                .append(Component.text("Punishment ID: ", NamedTextColor.GRAY))
                .append(Component.text("#" + punishment.getId(), NamedTextColor.WHITE));


        return kickMessage;
    }

    public Component getKickDisconnectMessage(Punishment punishment) {
        return Component.text("You were kicked from BuildTheEarth!", NamedTextColor.RED)
                .appendNewline()
                .appendNewline()
                .append(Component.text("Reason: ", NamedTextColor.GRAY))
                .append(Component.text(punishment.getReason(), NamedTextColor.WHITE))
                .appendNewline()
                .append(Component.text("Punishment ID: ", NamedTextColor.GRAY))
                .append(Component.text("#" + punishment.getId(), NamedTextColor.WHITE));
    }
}
