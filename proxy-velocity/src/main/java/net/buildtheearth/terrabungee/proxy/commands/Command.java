/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - Command.java
 */

package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

public abstract class Command implements SimpleCommand {
    public Command(String name, String node) {
        super();
    }

    public Command(String name, String node, String[] alias) {
        super();
    }

    protected boolean hasAdmin(CommandSource sender) {
        return CommandAuthorization.isAdmin(sender);
    }

    protected boolean hasModerator(CommandSource sender) {
        return CommandAuthorization.isModerator(sender);
    }
}
