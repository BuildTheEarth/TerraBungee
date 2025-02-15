/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - ICommandFragment.java
 */

package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.SimpleCommand;

public interface ICommandFragment {
    void execute(SimpleCommand.Invocation invocation);

    String getName();

    String getPurpose();

    String[] getArguments();
}
