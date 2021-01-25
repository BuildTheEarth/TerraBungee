/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - StatusFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments;

import com.noahhusby.terrabungee.api.Controller;
import com.noahhusby.terrabungee.proxy.Constants;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.chat.ChatHelper;
import com.noahhusby.terrabungee.proxy.chat.TextElement;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class StatusFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("TerraBungee Status:", ChatColor.BLUE)));
        Controller controller = TerraBungeeProxy.tb.getController();
        if(controller.isOnline()) {
            sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("Controller: ", ChatColor.GOLD),
                    new TextElement("Online", ChatColor.GREEN)));
            sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("System Status: ", ChatColor.GOLD),
                    new TextElement("Great", ChatColor.GREEN)));
            sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("Total Services: ", ChatColor.GRAY),
                    new TextElement(String.valueOf(controller.getTotalServices()), ChatColor.BLUE)));
            sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("Total Disconnected Services: ", ChatColor.GRAY),
                    new TextElement(String.valueOf(controller.getTotalDisconnectedServices()), ChatColor.BLUE)));
        } else {
            sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("Controller: ", ChatColor.GOLD),
                    new TextElement("Disconnected", ChatColor.RED)));
            sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("System Status: ", ChatColor.GOLD),
                    new TextElement("Fatal", ChatColor.RED)));
        }
        sender.sendMessage();
        sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("This Proxy:", ChatColor.BLUE)));
        sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("ID: ", ChatColor.GRAY),
                new TextElement(TerraBungeeProxy.tb.getId(), ChatColor.BLUE)));
        sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("Version: ", ChatColor.GRAY),
                new TextElement(Constants.version, ChatColor.BLUE)));
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getPurpose() {
        return "Shows status of the TB System";
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
