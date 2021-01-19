package com.noahhusby.terrabungee.controller.command.controller;

import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.command.ICommand;

public class StopCommand implements ICommand {
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
