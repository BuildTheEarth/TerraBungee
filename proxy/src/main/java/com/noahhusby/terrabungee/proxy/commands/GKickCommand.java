package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CBanPlayerPacket;
import net.buildtheearth.terrabungee.client.network.S2C.S2CKickPlayerPacket;
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
public class GKickCommand extends Command {
    public GKickCommand() {
        super("gkick", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.getNoPermission());
            return;
        }
        if(args.length < 2) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /gkick <player> <reason>"));
            return;
        }
        String player = args[0];

        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(player);
        playerFuture.thenAccept(tbPlayer -> {
           if(tbPlayer == null) {
               sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, player, ChatColor.GRAY, " has never joined the network!"));
               return;
           }
            StringBuilder reason = new StringBuilder();
            for(String r : Arrays.copyOfRange(args, 1, args.length)) {
                reason.append(r).append(" ");
            }
            reason = new StringBuilder(reason.toString().trim());
            CompletableFuture<Response> kickFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CKickPlayerPacket((sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), tbPlayer.getUniqueID(), reason.toString()));
            String finalReason = reason.toString();
            kickFuture.thenAccept(response -> {
               if(response.getCode() == Response.ResponseCode.ERROR) {
                   sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, tbPlayer.getName(), ChatColor.GRAY, " is not online!"));
               } else if (response.getCode() == Response.ResponseCode.SUCCESS){
                   sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Successfully kicked ", ChatColor.YELLOW, tbPlayer.getName(), ChatColor.GRAY, " for ", ChatColor.BLUE, finalReason));
               } else {
                   sender.sendMessage(ChatUtil.getNoContact());
               }
            });
        });
    }
}
