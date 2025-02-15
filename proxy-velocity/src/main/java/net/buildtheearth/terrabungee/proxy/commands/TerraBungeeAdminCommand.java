/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - TerraBungeeAdminCommand.java
 */

package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import net.buildtheearth.terrabungee.proxy.commands.fragments.InstanceFragment;
import net.buildtheearth.terrabungee.proxy.commands.fragments.StatusFragment;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;


public class TerraBungeeAdminCommand extends CommandFragmentManager {
    public TerraBungeeAdminCommand() {
        super("terrabungeeadmin", "", new String[]{ "tba" });
        setCommandBase("tba");
        setTitle("TerraBungee Admin Commands");

        registerCommandFragment(new StatusFragment());
        registerCommandFragment(new InstanceFragment());
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();

        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.NO_PERMISSION);
            return;
        }

        executeFragment(invocation);
    }
}
