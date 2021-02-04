package com.noahhusby.terrabungee.controller.command.controller;

import com.noahhusby.terrabungee.controller.command.ICommand;
import com.noahhusby.terrabungee.controller.config.ConfigHandler;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;

/**
 * @author Noah Husby
 */
public class MigrateCommand implements ICommand {
    @Override
    public String getName() {
        return "migrate";
    }

    @Override
    public String getPurpose() {
        return "Migrates data from the local storage to the sql database";
    }

    @Override
    public void execute(String[] args) {
        if(args.length == 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Type ", ConsoleColor.YELLOW, "migrate confirm",
                    ConsoleColor.BLUE, " to migrate the data!");
            return;
        }

        if(!args[1].equalsIgnoreCase("confirm")) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Type ", ConsoleColor.YELLOW, "migrate confirm",
                    ConsoleColor.BLUE, " to migrate the data!");
            return;
        }

        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully migrated data!");
        ConfigHandler.getInstance().migrate();
    }
}
