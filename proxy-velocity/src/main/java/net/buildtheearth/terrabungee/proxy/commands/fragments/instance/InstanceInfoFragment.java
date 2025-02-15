/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - InstanceInfoFragment.java
 */

package net.buildtheearth.terrabungee.proxy.commands.fragments.instance;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.commands.ICommandFragment;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.kyori.adventure.text.format.NamedTextColor;

public class InstanceInfoFragment implements ICommandFragment {
    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 1) {
            sender.sendMessage(ChatUtil.USAGE("/tba instance info <id>"));
            return;
        }

        for (Instance i : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
            if (i.getId().equalsIgnoreCase(args[0])) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "Instance Information:"));
                sender.sendMessage(ChatUtil.combine(NamedTextColor.GRAY, "ID: ", NamedTextColor.GOLD, i.getId()));
                sender.sendMessage(ChatUtil.combine(NamedTextColor.GRAY, "Type: ", NamedTextColor.BLUE, i.getInstanceType().name()));
                sender.sendMessage(ChatUtil.combine(NamedTextColor.GRAY, "Status: ", NamedTextColor.BLUE, i.getStatus()));
                sender.sendMessage(ChatUtil.combine(NamedTextColor.GRAY, "Address: ", NamedTextColor.BLUE, i.getAddress()));
                return;
            }
        }

        sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GRAY, "Could not find instance ",
                NamedTextColor.RED, args[0].toLowerCase(), NamedTextColor.GRAY, "!"));
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
