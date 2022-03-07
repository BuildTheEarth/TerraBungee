package net.buildtheearth.terrabungee.controller.command.instance;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;
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
        if (args.length < 2) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Usage: addstatic <id> <address>");
            return;
        }

        String id = args[0];
        String address = args[1];

        for (StorableStaticInstance s : InstanceManager.getInstance().getStaticInstances().values()) {
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
