package net.buildtheearth.terrabungee.controller.command;

import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.controller.console.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

/**
 * @author Noah Husby
 */
public class TestCommand extends Command {
    @Override
    public String getName() {
        return "att";
    }

    @Override
    public String getPurpose() {
        return "Gets attributes of player";
    }

    @Override
    public void execute(String[] args) {
        for (TBPlayer p : PlayerManager.getInstance().getPlayersRegistry().values()) {
            System.out.println(p.getUniqueID() + ", " + p.getName());
            if (p.getName().equalsIgnoreCase(args[1])) {
                TerraBungeeConsole.sendMessage(ConsoleColor.BLUE, TerraBungeeUtil.GSON.toJson(p.getAttributes()));
                return;
            }
        }

        TerraBungeeConsole.sendMessage(ConsoleColor.RED, "Couldn't find player!");
    }
}
