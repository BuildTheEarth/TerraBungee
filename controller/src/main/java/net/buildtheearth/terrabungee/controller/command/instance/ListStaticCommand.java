package net.buildtheearth.terrabungee.controller.command.instance;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;
import net.buildtheearth.terrabungee.controller.services.StorableStaticInstance;

public class ListStaticCommand extends Command {
    @Override
    public String getName() {
        return "liststatic";
    }

    @Override
    public String getPurpose() {
        return "Get a list of all the static instances";
    }

    @Override
    public void execute(String[] args) {

        TerraBungeeConsole.sendMessage(ConsoleColor.BLUE, "Static Instances:");

        for (StorableStaticInstance s : InstanceManager.getInstance().getStorableStaticInstances()) {
            TerraBungeeConsole.sendMessage(ConsoleColor.BLUE, s.id, ConsoleColor.WHITE, " - ",
                    ConsoleColor.RED, s.address);
        }
    }
}
