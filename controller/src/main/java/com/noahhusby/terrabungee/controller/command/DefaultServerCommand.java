package com.noahhusby.terrabungee.controller.command;

import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.console.TextComponent;
import com.noahhusby.terrabungee.controller.services.ServiceManager;

public class DefaultServerCommand implements ICommand {
    @Override
    public String getName() {
        return "setdefault";
    }

    @Override
    public void execute(String[] args) {
        if(args.length < 2) {
            TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.RED, "Usage: setdefault <id>"));
            return;
        }

        String id = args[1];

        boolean matched = false;
        for(ITerraBungeeService s : ServiceManager.getInstance().getServices()) {
            if(s.getId().equalsIgnoreCase(id)) {
                matched = true;
                if(s instanceof Instance) {
                    TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.GREEN, "Successfully set default instance/fleet "),
                            new TextComponent(ConsoleColor.BLUE, id));
                    ServiceManager.getInstance().setDefaultServer(id);
                    return;
                }
            }
        }

        if(matched) {
            TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.RED, "Service "),
                    new TextComponent(ConsoleColor.BLUE, id), new TextComponent(ConsoleColor.RED, " is not an instance/fleet!"));
            return;
        }

        TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.RED, "Service "),
                new TextComponent(ConsoleColor.BLUE, id), new TextComponent(ConsoleColor.RED, " doesn't exist!"));
    }
}
