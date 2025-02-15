/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - TerraBungeeCommand.java
 */

package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.common.Constants;
import net.kyori.adventure.text.format.NamedTextColor;

public class TerraBungeeCommand extends CommandFragmentManager {
    public TerraBungeeCommand() {
        super("terrabungee", "", new String[]{ "tb" });
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();

        sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "TerraBungee v" + Constants.VERSION,
                NamedTextColor.GRAY, " by ", NamedTextColor.BLUE, "BuildTheEarth"));
    }
}
