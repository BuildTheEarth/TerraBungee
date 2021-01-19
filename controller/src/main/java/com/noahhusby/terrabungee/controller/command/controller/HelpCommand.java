package com.noahhusby.terrabungee.controller.command.controller;

import com.noahhusby.terrabungee.controller.command.CommandManager;
import com.noahhusby.terrabungee.controller.command.ICommand;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.console.TextComponent;

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
        TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.RED, "Commands:"));
        for(ICommand command : CommandManager.getInstance().getCommands()) {
            TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.YELLOW, command.getName()),
                    new TextComponent(ConsoleColor.WHITE, " - "), new TextComponent(ConsoleColor.GREEN, command.getPurpose()));
        }

    }
}
