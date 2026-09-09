/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - StatusFragment.java
 */

package net.buildtheearth.terrabungee.proxy.commands.fragments;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.commands.ICommandFragment;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.client.util.TBStats;
import net.buildtheearth.terrabungee.common.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class StatusFragment implements ICommandFragment {

    @Override
    public void execute(CommandSource sender, String[] args) {
        sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "TerraBungee Status:"));
        TBStats stats = TerraBungeeProxy.getInstance().getTerraBungee().getStats();
        TBStats.ControllerStats controller = stats.getControllerStats();
        if (controller.isOnline()) {
            sender.sendMessage(ChatUtil.combine(NamedTextColor.GOLD, "Controller: ", NamedTextColor.GREEN, "Online"));
            sender.sendMessage(ChatUtil.combine(NamedTextColor.GOLD, "System Status: ", NamedTextColor.GREEN, "Great"));
            sender.sendMessage(ChatUtil.combine(NamedTextColor.GOLD, "Version: ", NamedTextColor.GREEN, controller.getVersion()));
            sender.sendMessage(ChatUtil.combine(NamedTextColor.GRAY, "Total Services: ", NamedTextColor.BLUE, stats.getTotalServices()));
            sender.sendMessage(ChatUtil.combine(NamedTextColor.GRAY, "Total Disconnected Services: ", NamedTextColor.BLUE, stats.getTotalDisconnectedServices()));
        } else {
            sender.sendMessage(ChatUtil.combine(NamedTextColor.GOLD, "Controller: ", NamedTextColor.RED, "Disconnected"));
        }

        sender.sendMessage(Component.text());
        sender.sendMessage(ChatUtil.combine(NamedTextColor.RED, "This Proxy:"));
        sender.sendMessage(ChatUtil.combine(NamedTextColor.GRAY, "ID: ", NamedTextColor.BLUE, TerraBungeeProxy.getInstance().getTerraBungee().getId()));
        sender.sendMessage(ChatUtil.combine(NamedTextColor.GRAY, "Version: ", NamedTextColor.BLUE, Constants.VERSION));
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
