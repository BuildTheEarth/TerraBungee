/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - Command.java
 */

package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

public abstract class Command implements SimpleCommand {
    private String permissionNode;
    private String name;

    public Command(String name, String node) {
        super();
        this.name = name;
        this.permissionNode = node;
    }

    public Command(String name, String node, String[] alias) {
        super();
        this.name = name;
        this.permissionNode = node;
    }

    protected boolean hasAdmin(CommandSource sender) {
        return sender.hasPermission("terrabungee.admin");
    }

    protected boolean hasPermissionAdmin(CommandSource sender) {
        return sender.hasPermission("terrabungee.admin") || sender.hasPermission(permissionNode + ".admin");
    }
}
