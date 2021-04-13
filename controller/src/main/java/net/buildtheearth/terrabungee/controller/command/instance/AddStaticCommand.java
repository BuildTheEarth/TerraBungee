package net.buildtheearth.terrabungee.controller.command.instance;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.controller.console.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;
import net.buildtheearth.terrabungee.controller.services.StorableStaticInstance;

public class AddStaticCommand extends Command {

    @Override
    public String getName() {
        return "addstatic";
    }

    @Override
    public String getPurpose() {
        return "Add a static instance";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Usage: addstatic <id> <address>");
            return;
        }

        String id = args[1];
        String address = args[2];

        for (StorableStaticInstance s : InstanceManager.getInstance().getStorableStaticInstances()) {
            if (s.id.equalsIgnoreCase(id)) {
                TerraBungeeConsole.sendMessage(ConsoleColor.RED, "The static instance ", ConsoleColor.BLUE,
                        id, ConsoleColor.RED, " already exists!");
                return;
            }
        }

        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully added static instance ",
                ConsoleColor.BLUE, id, ConsoleColor.GREEN, " with address ", ConsoleColor.BLUE, address);
        InstanceManager.getInstance().addStaticInstance(null, id, address);
    }
}
