package com.noahhusby.terrabungee.proxy.commands;

import com.google.common.collect.Lists;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.players.PlayerHandler;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import com.noahhusby.terrabungee.proxy.util.ProxyUtil;
import net.buildtheearth.terrabungee.client.PlayerManager;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FindCommand extends Command implements TabExecutor {
    public FindCommand() {
        super("find", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.getNoPermission());
            return;
        }
        if(args.length < 1) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /find <username>"));
            return;
        }
        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(args[0]);
        playerFuture.thenAccept(tbPlayer -> {
           if(tbPlayer == null) {
               sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, args[0], " has never joined the network!"));
               return;
           }
           if(!tbPlayer.isOnline()) {
               sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, tbPlayer.getName(), " is not online!"));
               return;
           }
           sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, tbPlayer.getName(), ChatColor.GRAY, " is currently on proxy ", ChatColor.BLUE, tbPlayer.getProxy(), ChatColor.GRAY, " on server ", tbPlayer.getServer()));
           return;
        });
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completion = new ArrayList<>();
        ProxyUtil.copyPartialMatches(args[0], PlayerHandler.getInstance().getOnlinePlayerNames(), completion);
        return completion;
    }
}
