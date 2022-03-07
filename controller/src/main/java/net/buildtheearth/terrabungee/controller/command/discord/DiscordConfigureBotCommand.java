package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.discord.BotConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author Noah Husby
 */
public class DiscordConfigureBotCommand extends Command {
    @Override
    public String getName() {
        return "configurebot";
    }

    @Override
    public String getPurpose() {
        return "Configures a discord bot";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2 || !(args[1].equalsIgnoreCase("token") || args[1].equalsIgnoreCase("name"))) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "/configurebot <id> <token | name>");
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
        String command = args[1].toLowerCase(Locale.ROOT);
        if (command.equals("token")) {
            if (args.length < 3) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "/configurebot <id> token <token>");
                return;
            }
            String token = args[2];
            BotConfig config = DiscordManager.getInstance().getBotConfigs().get(id);
            config.setToken(token);
            DiscordManager.getInstance().getBotConfigs().put(config.getId(), config);
            DiscordManager.getInstance().getBotConfigs().save();
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Successfully set token of " + ConsoleColor.YELLOW + config.getName());
        } else if (command.equals("name")) {
            if (args.length < 3) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "/configurebot <id> token <name>");
                return;
            }
            BotConfig config = DiscordManager.getInstance().getBotConfigs().get(id);
            StringBuilder nameStringBuilder = new StringBuilder();
            for (String arg : Arrays.copyOfRange(args, 2, args.length)) {
                nameStringBuilder.append(arg).append(" ");
            }
            String name = nameStringBuilder.toString().trim();
            config.setName(name);
            DiscordManager.getInstance().getBotConfigs().put(config.getId(), config);
            DiscordManager.getInstance().getBotConfigs().save();
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Successfully set name of " + ConsoleColor.YELLOW + config.getName());
        }
    }
}
