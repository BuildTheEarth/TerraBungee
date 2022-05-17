package com.noahhusby.terrabungee.proxy.commands;

import com.google.common.collect.Lists;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import com.noahhusby.terrabungee.proxy.util.ProxyUtil;
import net.buildtheearth.terrabungee.instance.Instance;
import net.buildtheearth.terrabungee.instance.InstanceType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class ServerCommand extends Command implements TabExecutor {
    public ServerCommand() {
        super("server", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.getNoPermission());
            return;
        }
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatUtil.getPlayerOnly());
            return;
        }
        if (args.length < 1) {
            TextComponent list = ChatUtil.titleAndCombine(ChatColor.RED, "Servers: ");
            boolean first = true;
            for (Instance i : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
                if (first) {
                    first = false;
                } else {
                    list.addExtra(ChatUtil.combine(ChatColor.GRAY, ", "));
                }

                TextComponent t = ChatUtil.combine((i.getInstanceType() == InstanceType.STATIC ?
                        ChatColor.GOLD : ChatColor.GREEN), i.getId());
                t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/server %s", i.getId())));
                t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Connect to " + i.getId()).create()));
                list.addExtra(t);
            }

            sender.sendMessage(list);
        } else {
            String server = args[0];
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
            if (serverInfo == null) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, server, ChatColor.RED, " does not exist!"));
                return;
            }
            ((ProxiedPlayer) sender).connect(serverInfo);
        }
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completion = new ArrayList<>();
        List<String> servers = Lists.newArrayList();
        for (Instance instance : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
            servers.add(instance.getId());
        }
        ProxyUtil.copyPartialMatches(args[0], servers, completion);
        return completion;
    }
}
