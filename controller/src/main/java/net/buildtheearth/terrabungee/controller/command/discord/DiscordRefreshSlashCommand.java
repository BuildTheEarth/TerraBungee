package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;

/**
 * @author Noah Husby
 */
public class DiscordRefreshSlashCommand extends Command {
    @Override
    public String getName() {
        return "refreshslash";
    }

    @Override
    public String getPurpose() {
        return "Refresh slash commands";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Usage: /refreshslash <bot id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That is not a valid ID number!");
            return;
        }
        if (!DiscordManager.getInstance().getBotConfigs().containsKey(id)) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That bot does not exist!");
            return;
        }
        DiscordManager.getInstance().updateSlashCommands(DiscordManager.getInstance().getBotConfigs().get(id));
        TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "Attempting slash command refresh!");
    }
}
