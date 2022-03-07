package net.buildtheearth.terrabungee.controller.command.controller;

import com.google.common.collect.Lists;
import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeVersion;
import net.buildtheearth.terrabungee.common.services.Service;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

import java.util.List;

/**
 * @author Noah Husby
 */
public class VersionCommand extends Command {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getPurpose() {
        return "Shows version information";
    }

    @Override
    public void execute(String[] args) {
        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Controller Version: ", ConsoleColor.WHITE, Constants.VERSION);
        TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Use: 'version -all' ", ConsoleColor.YELLOW, "to retrieve version information for the whole system");
        TerraBungeeConsole.sendMessage();

        List<String> oldServices = Lists.newArrayList();
        List<String> newServices = Lists.newArrayList();
        List<String> currentServices = Lists.newArrayList();

        for (Service service : ServiceManager.getInstance().getServices().values()) {
            TerraBungeeVersion version = service.getVersion();
            if (version != null) {
                if (version.isOlder(Constants.VERSION)) {
                    oldServices.add(ConsoleColor.CYAN + service.getId() + ConsoleColor.RED + " [" + version + "]");
                } else if (version.isNewer(Constants.VERSION)) {
                    newServices.add(ConsoleColor.CYAN + service.getId() + ConsoleColor.RED + " [" + version + "]");
                } else {
                    currentServices.add(ConsoleColor.CYAN + service.getId() + ConsoleColor.RED + " [" + version + "]");
                }
            }
        }

        boolean all = args.length > 0 && args[0].equalsIgnoreCase("-all");

        TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW, "There are ", ConsoleColor.GREEN, oldServices.size(), ConsoleColor.YELLOW, " outdated clients.");
        if (all) {
            for (String info : oldServices) {
                TerraBungeeConsole.sendMessage(info);
            }
            TerraBungeeConsole.sendMessage();
        }
        TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW, "There are ", ConsoleColor.GREEN, newServices.size(), ConsoleColor.YELLOW, " newer clients.");
        if (all) {
            for (String info : newServices) {
                TerraBungeeConsole.sendMessage(info);
            }
            TerraBungeeConsole.sendMessage();
        }
        TerraBungeeConsole.sendMessage(ConsoleColor.YELLOW, "There are ", ConsoleColor.GREEN, currentServices.size(), ConsoleColor.YELLOW, " up-to-date clients.");
        if (all) {
            for (String info : currentServices) {
                TerraBungeeConsole.sendMessage(info);
            }
            TerraBungeeConsole.sendMessage();
        }

    }
}
