/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - InstanceInfoFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class InstanceInfoFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /tba instance info <id>"));
            return;
        }

        for (Instance i : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
            if (i.getId().equalsIgnoreCase(args[0])) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Instance Information:"));
                sender.sendMessage(ChatUtil.combine(ChatColor.GRAY, "ID: ", ChatColor.GOLD, i.getId()));
                sender.sendMessage(ChatUtil.combine(ChatColor.GRAY, "Type: ", ChatColor.BLUE, i.getInstanceType().name()));
                sender.sendMessage(ChatUtil.combine(ChatColor.GRAY, "Status: ", ChatColor.BLUE, i.getStatus()));
                sender.sendMessage(ChatUtil.combine(ChatColor.GRAY, "Address: ", ChatColor.BLUE, i.getAddress()));
                return;
            }
        }

        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Could not find instance ",
                ChatColor.RED, args[0].toLowerCase(), ChatColor.GRAY, "!"));
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getPurpose() {
        return "Display info about an instance";
    }

    @Override
    public String[] getArguments() {
        return new String[]{ "<id>" };
    }
}
