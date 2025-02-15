package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import com.noahhusby.terrabungee.proxy.util.DateUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CBanPlayerPacket;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Noah Husby
 */
public class GBanCommand extends Command {
    public GBanCommand() {
        super("gban", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.getNoPermission());
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /gban <player> <length> <reason>"));
            return;
        }
        String player = args[0];
        String lengthString = args[1];
        long length;
        try {
            if (lengthString.equals("0")) {
                length = 0;
            } else {
                length = DateUtil.parseDateDiff(lengthString, true, true);
            }
        } catch (Exception ignored) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.BLUE, lengthString, ChatColor.GRAY, " is not a valid length! Enter the amount of time for the ban (Ex: 1d12h), or enter ", ChatColor.YELLOW, "0 ", ChatColor.GRAY, "for a permanent ban."));
            return;
        }
        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(player);
        playerFuture.thenAccept(tbPlayer -> {
            if (tbPlayer == null) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, player, ChatColor.GRAY, " has never joined the network!"));
                return;
            }
            StringBuilder reason = new StringBuilder();
            for (String r : Arrays.copyOfRange(args, 2, args.length)) {
                reason.append(r).append(" ");
            }
            reason = new StringBuilder(reason.toString().trim());
            CompletableFuture<Response> banFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CBanPlayerPacket((sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), tbPlayer.getUniqueID(), length, reason.toString()));
            String finalReason = reason.toString();
            banFuture.thenAccept(response -> {
                if (response.getCode() == Response.ResponseCode.ERROR) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, tbPlayer.getName(), ChatColor.GRAY, " was already banned!"));
                } else if (response.getCode() == Response.ResponseCode.SUCCESS) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Successfully banned ", ChatColor.YELLOW, tbPlayer.getName(), lengthString.equals("0") ? " permanently" : " for " + DateUtil.getExpandedTimeMessage(length), ChatColor.GRAY, " for ", ChatColor.BLUE, finalReason));
                } else {
                    sender.sendMessage(ChatUtil.getNoContact());
                }
            });
        });
    }
}
