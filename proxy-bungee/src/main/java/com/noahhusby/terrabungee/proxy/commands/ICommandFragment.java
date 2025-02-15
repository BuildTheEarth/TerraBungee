/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - ICommandFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands;

import net.md_5.bungee.api.CommandSender;

public interface ICommandFragment {
    void execute(CommandSender sender, String[] args);

    String getName();

    String getPurpose();

    String[] getArguments();
}
