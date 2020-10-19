package com.noahhusby.terrabungee.controller.command;

import com.noahhusby.terrabungee.controller.TerraBungeeController;

public class StopCommand implements ICommand {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public void execute(String[] args) {
        TerraBungeeController.isTerraBungeeRunning = false;
        TerraBungeeController.logger.warning("Shutting down the TerraBungee Controller!");
    }
}
