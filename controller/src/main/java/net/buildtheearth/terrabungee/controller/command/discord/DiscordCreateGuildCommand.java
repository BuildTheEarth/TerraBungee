package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.BotConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.GuildConfig;

/**
 * @author Noah Husby
 */
public class DiscordCreateGuildCommand extends Command {

    @Override
    public String getName() {
        return "createguild";
    }

    @Override
    public String getPurpose() {
        return "Create a new guild";
    }

    @Override
    public void execute(String[] args) {
        if(args.length < 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Usage: /createguild <guild id>");
            return;
        }
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (Exception e) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That is not a valid ID number!");
            return;
        }
        if(DiscordManager.getInstance().getGuildConfigs().containsKey(id)) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That guild already exists!");
            return;
        }
        GuildConfig config = new GuildConfig(id);
        DiscordManager.getInstance().getGuildConfigs().put(config.getGuildId(), config);
        DiscordManager.getInstance().getGuildConfigs().saveAsync();
        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN + "Created guild config for " + ConsoleColor.YELLOW + id);
        TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Use " + ConsoleColor.YELLOW + "/configureguild " + id + ConsoleColor.RED + " to configure the guild.");

    }
}
