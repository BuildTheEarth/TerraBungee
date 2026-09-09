/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - StatusFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.client.util.TBStats;
import net.buildtheearth.terrabungee.common.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class StatusFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "TerraBungee Status:"));
        TBStats stats = TerraBungeeProxy.getInstance().getTerraBungee().getStats();
        TBStats.ControllerStats controller = stats.getControllerStats();
        if (controller.isOnline()) {
            sender.sendMessage(ChatUtil.combine(ChatColor.GOLD, "Controller: ", ChatColor.GREEN, "Online"));
            sender.sendMessage(ChatUtil.combine(ChatColor.GOLD, "System Status: ", ChatColor.GREEN, "Great"));
            sender.sendMessage(ChatUtil.combine(ChatColor.GOLD, "Version: ", ChatColor.GREEN, controller.getVersion()));
            sender.sendMessage(ChatUtil.combine(ChatColor.GRAY, "Total Services: ", ChatColor.BLUE, stats.getTotalServices()));
            sender.sendMessage(ChatUtil.combine(ChatColor.GRAY, "Total Disconnected Services: ", ChatColor.BLUE, stats.getTotalDisconnectedServices()));
        } else {
            sender.sendMessage(ChatUtil.combine(ChatColor.GOLD, "Controller: ", ChatColor.RED, "Disconnected"));
        }

        sender.sendMessage();
        sender.sendMessage(ChatUtil.combine(ChatColor.RED, "This Proxy:"));
        sender.sendMessage(ChatUtil.combine(ChatColor.GRAY, "ID: ", ChatColor.BLUE, TerraBungeeProxy.getInstance().getTerraBungee().getId()));
        sender.sendMessage(ChatUtil.combine(ChatColor.GRAY, "Version: ", ChatColor.BLUE, Constants.VERSION));
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getPurpose() {
        return "Shows status of the TB System";
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
