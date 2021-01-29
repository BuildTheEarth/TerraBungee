package com.noahhusby.terrabungee.controller.command;

import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.api.players.TBPlayer;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.console.TextComponent;
import com.noahhusby.terrabungee.controller.players.PlayerManager;

/**
 * @author Noah Husby
 */
public class TestCommand implements ICommand {
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
        for(TBPlayer p : PlayerManager.getInstance().getPlayersRegistry().values()) {
            System.out.println(p.getUniqueID() + ", " + p.getName());
            if(p.getName().equalsIgnoreCase(args[1])) {
                TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.BLUE, TerraBungeeUtil.GSON.toJson(p.getAttributes())));
                return;
            }
        }

        TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.RED, "Couldn't find player!"));
    }
}
