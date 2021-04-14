package net.buildtheearth.terrabungee.controller.command.instance;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;
import net.buildtheearth.terrabungee.controller.services.StorableStaticInstance;

public class RemoveStaticCommand extends Command {
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
        if (args.length < 2) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Usage: removestatic <id>");
            return;
        }

        String id = args[1];

        StorableStaticInstance remove = null;
        for (StorableStaticInstance s : InstanceManager.getInstance().getStorableStaticInstances()) {
            if (s.id.equalsIgnoreCase(id)) {
                remove = s;
            }
        }

        if (remove != null) {
            InstanceManager.getInstance().removeStaticInstance(null, id);
            TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully removed static instance ",
                    ConsoleColor.BLUE, id);
            return;
        }

        TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Static instance ", ConsoleColor.BLUE, id,
                ConsoleColor.RED, " doesn't exist!");
    }
}
