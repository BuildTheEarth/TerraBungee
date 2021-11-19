package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.BotConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;

/**
 * @author Noah Husby
 */
public class DiscordCreateBotCommand extends Command {

    @Override
    public String getName() {
        return "createbot";
    }

    @Override
    public String getPurpose() {
        return "Create a new bot";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Usage: /createbot <name>");
            return;
        }
        StringBuilder nameStringBuilder = new StringBuilder();
        for (String arg : args) {
            nameStringBuilder.append(arg).append(" ");
        }
        String name = nameStringBuilder.toString().trim();
        BotConfig config = DiscordManager.getInstance().createBot(name);
        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN + "Created " + ConsoleColor.YELLOW + name + ConsoleColor.GREEN + " with ID " + ConsoleColor.RED + config.getId());
        TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Use " + ConsoleColor.YELLOW + "/configurebot " + config.getId() + ConsoleColor.RED + " to configure the bot.");
    }
}
