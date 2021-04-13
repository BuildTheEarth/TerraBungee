package net.buildtheearth.terrabungee.controller.command.instance;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.console.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

public class DefaultServerCommand extends Command {
    @Override
    public String getName() {
        return "setdefault";
    }

    @Override
    public String getPurpose() {
        return "Set the default server/fleet upon joining";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Usage: setdefault <id>");
            return;
        }

        String id = args[1];

        boolean matched = false;
        for (TerraBungeeService s : ServiceManager.getInstance().getServices().values()) {
            if (s.getId().equalsIgnoreCase(id)) {
                matched = true;
                if (s instanceof Instance) {
                    TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully set default instance/fleet ",
                            ConsoleColor.BLUE, id);
                    ServiceManager.getInstance().setDefaultServer(id);
                    return;
                }
            }
        }

        if (matched) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Service ", ConsoleColor.BLUE, id, ConsoleColor.RED,
                    " is not an instance/fleet!");
            return;
        }

        TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Service ", ConsoleColor.BLUE, id,
                ConsoleColor.RED, " doesn't exist!");
    }
}
