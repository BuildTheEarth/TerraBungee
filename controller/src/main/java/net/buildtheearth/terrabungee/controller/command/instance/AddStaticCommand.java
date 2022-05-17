package net.buildtheearth.terrabungee.controller.command.instance;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.logging.ConsoleColor;
import net.buildtheearth.terrabungee.controller.instance.InstanceManager;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;

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

        if (!TerraBungeeUtil.validateServerTag(id)) {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "That is an invalid server id. Please make sure the id contains only letters, numbers, or dashes and is between 3-24 characters long.");
            return;
        }

        if (InstanceManager.getInstance().addStaticInstance(null, id, address)) {
            TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, "Successfully added static instance ",
                    ConsoleColor.BLUE, id, ConsoleColor.GREEN, " with address ", ConsoleColor.BLUE, address);
        } else {
            TerraBungeeConsole.sendMessage(ConsoleColor.RED, "The static instance ", ConsoleColor.BLUE,
                    id, ConsoleColor.RED, " already exists!");
        }
    }
}
