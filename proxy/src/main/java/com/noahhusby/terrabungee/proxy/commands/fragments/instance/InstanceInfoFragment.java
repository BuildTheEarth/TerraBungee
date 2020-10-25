/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - InstanceInfoFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxyMain;
import com.noahhusby.terrabungee.proxy.chat.ChatHelper;
import com.noahhusby.terrabungee.proxy.chat.TextElement;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class InstanceInfoFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Usage: /tba instance info <id>", ChatColor.RED)));
            return;
        }

        for(Instance i : TerraBungeeProxyMain.tb.getInstanceManager().getInstances())
            if(i.getId().equalsIgnoreCase(args[0])) {
                sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Instance [", ChatColor.GRAY),
                        new TextElement(i.getId(), ChatColor.BLUE), new TextElement("]:", ChatColor.GRAY)));
                sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("ID: ", ChatColor.GREEN), new TextElement(i.getId(), ChatColor.RED)));
                sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("Type: ", ChatColor.GREEN), new TextElement(i.getInstanceType().name(), ChatColor.RED)));
                sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("Status: ", ChatColor.GREEN), new TextElement(i.getStatus().name(), ChatColor.RED)));
                sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("Address: ", ChatColor.GREEN), new TextElement(i.getAddress(), ChatColor.RED)));
                return;
            }

        sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Could not find instance ", ChatColor.GRAY),
                new TextElement(args[0].toLowerCase(), ChatColor.BLUE)));
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getPurpose() {
        return "Display info about an instance";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"<id>"};
    }
}
