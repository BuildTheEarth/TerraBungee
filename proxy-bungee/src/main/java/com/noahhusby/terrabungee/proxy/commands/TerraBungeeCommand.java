/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - TerraBungeeCommand.java
 */

package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.common.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class TerraBungeeCommand extends CommandFragmentManager {
    public TerraBungeeCommand() {
        super("terrabungee", "", new String[]{ "tb" });
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "TerraBungee v" + Constants.VERSION,
                ChatColor.GRAY, " by ", ChatColor.BLUE, "Noah Husby"));
    }
}
