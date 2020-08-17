package com.saghetti.TerraBungeeProxy;

import com.saghetti.TerraBungeeAPI.Network;
import com.saghetti.TerraBungeeAPI.RemoteInstance;

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
