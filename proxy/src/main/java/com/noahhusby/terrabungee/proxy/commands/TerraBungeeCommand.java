package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.Constants;
import com.noahhusby.terrabungee.proxy.chat.ChatHelper;
import com.noahhusby.terrabungee.proxy.chat.TextElement;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class TerraBungeeCommand extends CommandFragmentManager {
    public TerraBungeeCommand() {
        super("terrabungee", "", new String[]{"tb"});
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("TerraBungee v"+ Constants.version, ChatColor.RED),
                new TextElement(" by ", ChatColor.GRAY), new TextElement("Noah Husby", ChatColor.BLUE)));
    }
}
