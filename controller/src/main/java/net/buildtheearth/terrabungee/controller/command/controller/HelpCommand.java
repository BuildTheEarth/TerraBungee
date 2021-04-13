package net.buildtheearth.terrabungee.controller.command.controller;

import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.controller.command.CommandManager;
import net.buildtheearth.terrabungee.controller.console.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;

public class HelpCommand extends Command {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPurpose() {
        return "Shows a list of all commands";
    }

    @Override
    public void execute(String[] args) {
        TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Commands:");
        for (Command command : CommandManager.getInstance().getCommands()) {
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW, command.getName(), ConsoleColor.WHITE, " - ",
                    ConsoleColor.GREEN, command.getPurpose());
        }
        for(Command command : TerraBungee.getInstance().getPluginManager().getCommandMap().values()) {
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW, command.getName(), ConsoleColor.WHITE, " - ",
                    ConsoleColor.GREEN, command.getPurpose());
        }
    }
}
