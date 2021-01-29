package com.noahhusby.terrabungee.controller.command.controller;

import com.noahhusby.terrabungee.controller.command.CommandManager;
import com.noahhusby.terrabungee.controller.command.ICommand;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;

public class HelpCommand implements ICommand {
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
        for(ICommand command : CommandManager.getInstance().getCommands()) {
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW, command.getName(), ConsoleColor.WHITE, " - ",
                    ConsoleColor.GREEN, command.getPurpose());
        }

    }
}
