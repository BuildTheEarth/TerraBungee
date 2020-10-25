/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - ListInstanceFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxyMain;
import com.noahhusby.terrabungee.proxy.chat.ChatHelper;
import com.noahhusby.terrabungee.proxy.chat.TextElement;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ListInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("TerraBungee Instances:", ChatColor.BLUE)));
        for(Instance i : TerraBungeeProxyMain.tb.getInstanceManager().getInstances())
            sender.sendMessage(ChatHelper.makeTextComponent(new TextElement(i.getId(), ChatColor.RED),
                    new TextElement(" (", ChatColor.GRAY), new TextElement(i.getInstanceType().name(), ChatColor.GOLD),
                    new TextElement(")", ChatColor.GRAY)));
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getPurpose() {
        return "List all instances";
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
