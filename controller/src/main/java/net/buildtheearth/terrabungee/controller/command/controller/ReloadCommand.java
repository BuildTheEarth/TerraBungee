package net.buildtheearth.terrabungee.controller.command.controller;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.controller.storage.StorageHandler;

/**
 * @author Noah Husby
 */
public class ReloadCommand extends Command {
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
        StorageHandler.getInstance().reload();
    }
}
