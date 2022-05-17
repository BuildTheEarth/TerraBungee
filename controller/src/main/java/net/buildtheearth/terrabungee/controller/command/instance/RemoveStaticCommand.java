package net.buildtheearth.terrabungee.controller.command.instance;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.common.logging.ConsoleColor;
import net.buildtheearth.terrabungee.controller.instance.InstanceManager;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;

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
        if (args.length < 1) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Usage: removestatic <id>");
            return;
        }

        String id = args[0];

        if (InstanceManager.getInstance().removeStaticInstance(null, id)) {
            TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully removed static instance ",
                    ConsoleColor.BLUE, id);
        } else {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Static instance ", ConsoleColor.BLUE, id,
                    ConsoleColor.RED, " doesn't exist!");
        }
    }
}
