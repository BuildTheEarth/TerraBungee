package com.noahhusby.terrabungee.controller.command.instance;

import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.controller.command.ICommand;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.services.ServiceManager;

public class DefaultServerCommand implements ICommand {
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
        if(args.length < 2) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Usage: setdefault <id>");
            return;
        }

        String id = args[1];

        boolean matched = false;
        for(ITerraBungeeService s : ServiceManager.getInstance().getServices()) {
            if(s.getId().equalsIgnoreCase(id)) {
                matched = true;
                if(s instanceof Instance) {
                    TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully set default instance/fleet ",
                            ConsoleColor.BLUE, id);
                    ServiceManager.getInstance().setDefaultServer(id);
                    return;
                }
            }
        }

        if(matched) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Service ", ConsoleColor.BLUE, id, ConsoleColor.RED,
                    " is not an instance/fleet!");
            return;
        }

        TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Service ", ConsoleColor.BLUE, id,
                ConsoleColor.RED, " doesn't exist!");
    }
}
