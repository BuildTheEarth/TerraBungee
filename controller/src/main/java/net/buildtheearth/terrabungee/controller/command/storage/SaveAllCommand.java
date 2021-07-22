package net.buildtheearth.terrabungee.controller.command.storage;

import com.noahhusby.lib.data.storage.Storage;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;

import java.util.Map;

/**
 * @author Noah Husby
 */
public class SaveAllCommand extends Command {
    @Override
    public String getName() {
        return "save-all";
    }

    @Override
    public String getPurpose() {
        return "Saves all storage";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Type ", ConsoleColor.YELLOW, "save-all confirm",
                    ConsoleColor.BLUE, " to save all data!");
            return;
        }

        if (!args[0].equalsIgnoreCase("confirm")) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Type ", ConsoleColor.YELLOW, "save-all confirm",
                    ConsoleColor.BLUE, " to save all data!");
            return;
        }

        for(Map.Entry<String, Storage> storageEntry : ConfigHandler.getInstance().getStorageMap().entrySet()) {
            try {
                TerraBungee.getInstance().getLogger().info("Saving storage: " + storageEntry.getKey());
                storageEntry.getValue().save();
            } catch (Exception e) {
                TerraBungee.getInstance().getLogger().info("Failed to save storage: " + storageEntry.getKey());
                e.printStackTrace();
            }
            TerraBungee.getInstance().getLogger().info("Saved storage: " + storageEntry.getKey());
        }
        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully saved all data!");
    }
}
