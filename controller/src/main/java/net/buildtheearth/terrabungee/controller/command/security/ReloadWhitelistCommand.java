package net.buildtheearth.terrabungee.controller.command.security;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.security.SecurityManager;

/**
 * @author Noah Husby
 */
public class ReloadWhitelistCommand extends Command {
    @Override
    public String getName() {
        return "reloadwhitelist";
    }

    @Override
    public String getPurpose() {
        return "Reloads the security whitelist";
    }

    @Override
    public void execute(String[] args) {
        SecurityManager.getInstance().loadWhitelist();
        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN + "Successfully reloaded security whitelist");
    }
}
