package net.buildtheearth.terrabungee.controller.command.controller;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.storage.StorageHandler;

/**
 * @author Noah Husby
 */
public class MigrateCommand extends Command {
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
        if (args.length == 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Type ", ConsoleColor.YELLOW, "migrate confirm",
                    ConsoleColor.BLUE, " to migrate the data!");
            return;
        }

        if (!args[1].equalsIgnoreCase("confirm")) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Type ", ConsoleColor.YELLOW, "migrate confirm",
                    ConsoleColor.BLUE, " to migrate the data!");
            return;
        }

        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully migrated data!");
        StorageHandler.getInstance().migrate();
    }
}
