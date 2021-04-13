package net.buildtheearth.terrabungee.controller.command.controller;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;

public class StopCommand extends Command {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getPurpose() {
        return "Stop the controller";
    }

    @Override
    public void execute(String[] args) {
        TerraBungeeController.isTerraBungeeRunning = false;
        TerraBungeeController.logger.warning("Shutting down the TerraBungee Controller!");
    }
}
