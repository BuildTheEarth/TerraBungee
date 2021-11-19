package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;

/**
 * @author Noah Husby
 */
public class DiscordRemoveGuildCommand extends Command {

    @Override
    public String getName() {
        return "removeguild";
    }

    @Override
    public String getPurpose() {
        return "Remove a guild";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Usage: /removeguild <guild id>");
            return;
        }
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (Exception e) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That is not a valid ID number!");
            return;
        }
        if (!DiscordManager.getInstance().getGuildConfigs().containsKey(id)) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That guild does not exist!");
            return;
        }
        DiscordManager.getInstance().getGuildConfigs().remove(id);
        DiscordManager.getInstance().getGuildConfigs().saveAsync();
        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN + "Successfully removed guild!");
    }
}
