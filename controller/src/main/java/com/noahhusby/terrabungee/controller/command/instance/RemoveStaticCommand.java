package com.noahhusby.terrabungee.controller.command.instance;

import com.noahhusby.terrabungee.controller.command.ICommand;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.services.InstanceManager;
import com.noahhusby.terrabungee.controller.services.StorableStaticInstance;

public class RemoveStaticCommand implements ICommand {
    @Override
    public String getName() {
        return "removestatic";
    }

    @Override
    public String getPurpose() {
        return "Remove a static instance";
    }

    @Override
    public void execute(String[] args) {
        if(args.length < 2) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Usage: removestatic <id>");
            return;
        }

        String id = args[1];

        StorableStaticInstance remove = null;
        for(StorableStaticInstance s : InstanceManager.getInstance().getStorableStaticInstances()) {
            if(s.id.equalsIgnoreCase(id)) {
                remove = s;
            }
        }

        if(remove != null) {
            InstanceManager.getInstance().removeStaticInstance(null, id);
            TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully removed static instance ",
                    ConsoleColor.BLUE, id);
            return;
        }

        TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Static instance ", ConsoleColor.BLUE, id,
                ConsoleColor.RED, " doesn't exist!");
    }
}
