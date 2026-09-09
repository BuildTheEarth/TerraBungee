package com.noahhusby.terrabungee.proxy;

import com.noahhusby.terrabungee.proxy.players.PlayerHandler;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import com.noahhusby.terrabungee.proxy.util.DateUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrieveActiveBanPacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * @author Noah Husby
 */
public class ProxyListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoginEvent(LoginEvent e) {
        try {
            Response punishmentResponse = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrieveActiveBanPacket(e.getConnection().getUniqueId())).get();
            if (punishmentResponse.getCode() == Response.ResponseCode.SUCCESS) {
                Punishment punishment = TerraBungeeUtil.GSON.fromJson(punishmentResponse.getData(), Punishment.class);
                e.getConnection().disconnect(PlayerHandler.getInstance().getBanDisconnectMessage(punishment));
            }
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEarlyChat(ChatEvent e) {
        if (e.isCancelled() || !(e.getSender() instanceof ProxiedPlayer) || e.getMessage().startsWith("/")) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        Punishment mute = PlayerHandler.getInstance().getMuteCache().get(player.getUniqueId());
        if (mute != null) {
            e.setMessage("");
            e.setCancelled(true);

            player.sendMessage();
            player.sendMessage();
            if (mute.getEnd() == null) {
                player.sendMessage(ChatUtil.combine(ChatColor.RED, "You have been muted permanently", ChatColor.GRAY, " for ", ChatColor.YELLOW, mute.getReason()));
            } else {
                player.sendMessage(ChatUtil.combine(ChatColor.RED, "You have been muted for ", ChatColor.RESET, DateUtil.getExpandedTimeMessage(LocalDateTime.parse(mute.getEnd()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - new Date().getTime()), ChatColor.GRAY, " for ", ChatColor.YELLOW, mute.getReason()));
            }
            player.sendMessage(ChatUtil.combine(ChatColor.GRAY, "Punishment ID: ", mute.getId()));
            player.sendMessage();
            player.sendMessage();
        }
    }

}
