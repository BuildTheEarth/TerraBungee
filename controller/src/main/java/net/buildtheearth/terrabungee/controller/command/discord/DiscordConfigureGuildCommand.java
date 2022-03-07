package net.buildtheearth.terrabungee.controller.command.discord;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.GuildConfig;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;

import java.util.Locale;

/**
 * @author Noah Husby
 */
public class DiscordConfigureGuildCommand extends Command {
    @Override
    public String getName() {
        return "configureguild";
    }

    @Override
    public String getPurpose() {
        return "Configures a discord guild";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2 || !(args[1].equalsIgnoreCase("bot") || args[1].equalsIgnoreCase("notification") || args[1].equals("role"))) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "/configureguild <guild id> <bot | notification | role>");
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
        String command = args[1].toLowerCase(Locale.ROOT);
        if (command.equals("bot")) {
            if (args.length < 3) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "/configureguild <guild id> bot <bot id>");
                return;
            }
            int botId;
            try {
                botId = Integer.parseInt(args[2]);
                if (!DiscordManager.getInstance().getBotConfigs().containsKey(botId)) {
                    throw new Exception();
                }
            } catch (Exception e) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That bot does not exist!");
                return;
            }
            GuildConfig config = DiscordManager.getInstance().getGuildConfigs().get(id);
            config.setBotId(botId);
            DiscordManager.getInstance().getGuildConfigs().put(config.getGuildId(), config);
            DiscordManager.getInstance().getGuildConfigs().save();
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Successfully set bot of " + ConsoleColor.YELLOW + config.getGuildId() + ConsoleColor.RED + " to " + ConsoleColor.GREEN + DiscordManager.getInstance().getBotConfigs().get(config.getBotId()).getName());
        } else if (command.equals("notification")) {
            if (args.length < 3) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "/configureguild <guild id> notification <notification id>");
                return;
            }
            long notificationId;
            try {
                notificationId = Long.parseLong(args[2]);
            } catch (Exception e) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That is not a valid notification id!");
                return;
            }
            GuildConfig config = DiscordManager.getInstance().getGuildConfigs().get(id);
            config.setNotificationChannel(notificationId);
            DiscordManager.getInstance().getGuildConfigs().put(config.getGuildId(), config);
            DiscordManager.getInstance().getGuildConfigs().save();
            TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Successfully set notification channel of " + ConsoleColor.YELLOW + config.getGuildId() + ConsoleColor.RED + " to " + ConsoleColor.GREEN + config.getNotificationChannel());
        } else if (command.equals("role")) {
            if (args.length < 4 || !(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove"))) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "/configureguild <guild id> role <add | remove>");
                return;
            }
            String subcommand = args[2].toLowerCase(Locale.ROOT);
            GuildConfig config = DiscordManager.getInstance().getGuildConfigs().get(id);
            long roleId;
            try {
                roleId = Long.parseLong(args[3]);
            } catch (Exception e) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "That is not a valid role id!");
                return;
            }
            if (subcommand.equals("add")) {
                config.getStaffRoles().add(roleId);
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Successfully set added role " + ConsoleColor.GREEN + roleId + ConsoleColor.RED + " to " + ConsoleColor.YELLOW + config.getGuildId());
            } else {
                config.getStaffRoles().remove(roleId);
                TerraBungeeConsole.sendMessage(ConsoleColor.RED + "Successfully removed role " + ConsoleColor.GREEN + roleId + ConsoleColor.RED + " from " + ConsoleColor.YELLOW + config.getGuildId());
            }
            DiscordManager.getInstance().getGuildConfigs().put(config.getGuildId(), config);
            DiscordManager.getInstance().getGuildConfigs().save();
        }
    }
}
