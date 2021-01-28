/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - TerraBungeeAdminCommand.java
 */

package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.commands.fragments.InstanceFragment;
import com.noahhusby.terrabungee.proxy.commands.fragments.StatusFragment;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class Thing extends Command {
    public Thing() {
        super("thing");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String name = args[0];
        TerraBungeeProxy.tb.getPlayer(name).thenAccept(player -> {
           if(player == null) {
               sender.sendMessage("Couldn't fetch player!");
               return;
           }

           sender.sendMessage(player.getName());
           sender.sendMessage(player.getUniqueID().toString());
           if(player.isOnline()) {
               sender.sendMessage(player.getServer());
           }
        });
    }
}
