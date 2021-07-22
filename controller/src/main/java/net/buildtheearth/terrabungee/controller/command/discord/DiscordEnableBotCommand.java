package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.BotConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author Noah Husby
 */
public class DiscordEnableBotCommand extends Command {
    @Override
    public String getName() {
        return "enablebot";
    }

    @Override
    public String getPurpose() {
        return "Enable/disable a specific bot";
    }

    @Override
    public void execute(String[] args) {
        if(args.length < 2 || !(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "/enablebot <id> <true/false>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That is not a valid ID number!");
            return;
        }
        if(!DiscordManager.getInstance().getBotConfigs().containsKey(id)) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That bot does not exist!");
            return;
        }
        String command = args[1].toLowerCase(Locale.ROOT);
        BotConfig config = DiscordManager.getInstance().getBotConfigs().get(id);
        if(!config.isConfigured()) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That bot is not configured yet!");
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Use " + ConsoleColor.YELLOW + "/configurebot " + config.getId() + ConsoleColor.RED + " to configure the bot.");
            return;
        }
        if(command.equals("true")) {
            if(config.isEnabled()) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + config.getName() + " is already enabled!");
            } else {
                try {
                    config.initBot();
                    TerraBungeeController.getInstance().getGeneralThreads().schedule(() -> DiscordManager.getInstance().updateSlashCommands(config), 3, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                    TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Failed to enable " + config.getName() + "! Please check the token and try again.");
                    return;
                }
                TerraBungeeConsole.sendMessage(ConsoleColor.GREEN + config.getName() + " is now enabled!");
            }
        } else {
            if(!config.isEnabled()) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + config.getName() + " is already disabled!");
            } else {
                try {
                    config.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                    TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Failed to disable " + config.getName() + "! Please try again later.");
                    return;
                }
                TerraBungeeConsole.sendMessage(ConsoleColor.GREEN + config.getName() + " is now disabled!");
            }
        }
    }
}
