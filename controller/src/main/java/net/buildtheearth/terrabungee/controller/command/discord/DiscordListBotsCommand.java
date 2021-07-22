package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.BotConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;

/**
 * @author Noah Husby
 */
public class DiscordListBotsCommand extends Command {
    @Override
    public String getName() {
        return "listbots";
    }

    @Override
    public String getPurpose() {
        return "Lists all bots";
    }

    @Override
    public void execute(String[] args) {
        TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Bots:");
        for(BotConfig config : DiscordManager.getInstance().getBotConfigs().values()) {
            int id = config.getId();
            String name = config.getName() == null ? "None" : config.getName();
            String token = config.getToken() == null ? "None" : config.getToken();
            TerraBungeeConsole.sendMessage("\n< >");
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "ID: " + id);
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "Status: " + (!config.isConfigured() ? ConsoleColor.RED + "Unconfigured" : ConsoleColor.GREEN + "Configured"));
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "Enabled: " + (!config.isEnabled() ? ConsoleColor.RED + "False" : ConsoleColor.GREEN + "True"));
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "Name: " + name);
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "Token: " + token);
            TerraBungeeConsole.sendMessage("< >\n");
        }
    }
}
