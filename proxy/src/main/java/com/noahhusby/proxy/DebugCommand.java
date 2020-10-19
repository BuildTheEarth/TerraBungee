/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * DebugCommand.java
 */

package com.noahhusby.proxy;

import com.noahhusby.TerraBungeeAPI.Network;
import com.noahhusby.TerraBungeeAPI.RemoteInstance;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class DebugCommand extends Command {
	public DebugCommand() {
		super("tbdebug");
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (args.length == 0) {
			commandSender.sendMessage(new TextComponent("You must specify a subcommand!"));
			return;
		}
		if (args[0].equalsIgnoreCase("listinstances")) {
			Network network = TerraBungeeProxyMain.getInstance().network;
			commandSender.sendMessage(new TextComponent("Instances: "));
			for (RemoteInstance instance : network.getAllInstancesStatic()) {
				commandSender.sendMessage(new TextComponent(instance.toString()));
			}
		}
	}
}
