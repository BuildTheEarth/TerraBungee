/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - ICommandFragment.java
 */

package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

public interface ICommandFragment {
    void execute(CommandSource sender, String[] args);

    String getName();

    String getPurpose();

    String[] getArguments();
}
