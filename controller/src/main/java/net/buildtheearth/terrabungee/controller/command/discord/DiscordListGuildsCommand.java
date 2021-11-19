package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.GuildConfig;

/**
 * @author Noah Husby
 */
public class DiscordListGuildsCommand extends Command {
    @Override
    public String getName() {
        return "listguilds";
    }

    @Override
    public String getPurpose() {
        return "Lists all guilds";
    }

    @Override
    public void execute(String[] args) {
        TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Guilds:");
        for (GuildConfig config : DiscordManager.getInstance().getGuildConfigs().values()) {
            TerraBungeeConsole.sendMessage("\n< >");
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "ID: " + config.getGuildId());
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "Status: " + (!config.isConfigured() ? ConsoleColor.RED + "Unconfigured" : ConsoleColor.GREEN + "Configured"));
            TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW + "Notification: " + config.getNotificationChannel());
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Roles:");
            for (Long role : config.getStaffRoles()) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + String.valueOf(role));

            }
            TerraBungeeConsole.sendMessage("< >\n");
        }
    }
}
