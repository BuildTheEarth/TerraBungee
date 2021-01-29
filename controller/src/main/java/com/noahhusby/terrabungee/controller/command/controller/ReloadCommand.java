package com.noahhusby.terrabungee.controller.command.controller;

import com.noahhusby.terrabungee.controller.command.ICommand;
import com.noahhusby.terrabungee.controller.config.ConfigHandler;

/**
 * @author Noah Husby
 */
public class ReloadCommand implements ICommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPurpose() {
        return "Reloads the controller";
    }

    @Override
    public void execute(String[] args) {
        ConfigHandler.getInstance().reload();
    }
}
