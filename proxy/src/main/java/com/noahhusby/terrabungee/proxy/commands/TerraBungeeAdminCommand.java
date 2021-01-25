/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - TerraBungeeAdminCommand.java
 */

package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.commands.fragments.InstanceFragment;
import com.noahhusby.terrabungee.proxy.commands.fragments.StatusFragment;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;

public class TerraBungeeAdminCommand extends CommandFragmentManager {
    public TerraBungeeAdminCommand() {
        super("terrabungeeadmin", "", new String[]{"tba"});
        setCommandBase("tba");
        setTitle("TerraBungee Admin Commands");

        registerCommandFragment(new StatusFragment());
        registerCommandFragment(new InstanceFragment());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.getNoPermission());
            return;
        }

        executeFragment(sender, args);
    }
}
