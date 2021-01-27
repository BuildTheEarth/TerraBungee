package com.noahhusby.terrabungee.controller.command;

import com.noahhusby.terrabungee.api.players.TBPlayer;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.players.PlayerManager;

/**
 * @author Noah Husby
 */
public class TestCommand implements ICommand {
    @Override
    public String getName() {
        return "players";
    }

    @Override
    public String getPurpose() {
        return "Shows players";
    }

    @Override
    public void execute(String[] args) {
        for(TBPlayer p : PlayerManager.getInstance().getOnlinePlayerRegistry().values())
            TerraBungeeController.logger.info(ConsoleColor.GREEN + p.getName() + ConsoleColor.WHITE + " - "
                + ConsoleColor.RED + p.getServer());
    }
}
