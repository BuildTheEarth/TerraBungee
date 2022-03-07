package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.discord.BotConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;

/**
 * @author Noah Husby
 */
public class DiscordRemoveBotCommand extends Command {

    @Override
    public String getName() {
        return "removebot";
    }

    @Override
    public String getPurpose() {
        return "Remove a bot";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Usage: /removebot <id>");
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
        BotConfig config = DiscordManager.getInstance().getBotConfigs().get(id);
        config.shutdown();
        String name = config.getName();
        DiscordManager.getInstance().getBotConfigs().remove(id);
        DiscordManager.getInstance().getBotConfigs().saveAsync();
        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN + "Successfully removed " + name + "!");
    }
}
