package com.noahhusby.terrabungee.controller.command.instance;

import com.noahhusby.terrabungee.controller.command.ICommand;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.console.TextComponent;
import com.noahhusby.terrabungee.controller.services.InstanceManager;
import com.noahhusby.terrabungee.controller.services.StorableStaticInstance;

public class ListStaticCommand implements ICommand {
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

        TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.BLUE, "Static Instances:"));

        for(StorableStaticInstance s : InstanceManager.getInstance().storableStaticInstances) {
            TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.BLUE, s.id), new TextComponent(ConsoleColor.WHITE, " - "),
                    new TextComponent(ConsoleColor.RED, s.address));
        }
    }
}
