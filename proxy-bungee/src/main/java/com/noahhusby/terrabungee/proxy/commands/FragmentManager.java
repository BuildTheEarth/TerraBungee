/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - FragmentManager.java
 */

package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class FragmentManager {

    List<ICommandFragment> commandFragments = new ArrayList<>();
    private String title = "";
    private String commandBase = "";

    protected void registerCommandFragment(ICommandFragment c) {
        commandFragments.add(c);
    }

    protected void setTitle(String t) {
        this.title = t;
    }

    protected void setCommandBase(String b) {
        this.commandBase = "/" + b + " ";
    }

    protected void executeFragment(CommandSender sender, String[] args) {
        executeFragment(sender, args, 0);
    }

    protected void executeFragment(CommandSender sender, String[] args, int index) {
        if (args.length <= index) {
            displayCommands(sender);
        } else {
            if (index == 0) {
                ArrayList<String> dataList = new ArrayList<>();
                for (int x = 1; x < args.length; x++) {
                    dataList.add(args[x]);
                }

                String[] data = dataList.toArray(new String[dataList.size()]);
                for (ICommandFragment f : commandFragments) {
                    if (f.getName().equals(args[0].toLowerCase())) {
                        f.execute(sender, data);
                        return;
                    }
                }
                displayCommands(sender);
            } else {
                for (ICommandFragment f : commandFragments) {
                    if (f.getName().equals(args[index].toLowerCase())) {
                        f.execute(sender, args);
                        return;
                    }
                }
                displayCommands(sender);
            }
        }
    }

    private void displayCommands(CommandSender sender) {
        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, title + ":"));
        for (ICommandFragment f : commandFragments) {

            TextComponent message = new TextComponent();
            message.addExtra(ChatUtil.combine(ChatColor.YELLOW, commandBase));
            message.addExtra(ChatUtil.combine(ChatColor.GREEN, String.format("%s ", f.getName())));
            if (f.getArguments() != null) {
                for (int x = 0; x < f.getArguments().length; x++) {
                    String argument = f.getArguments()[x];
                    if (argument.startsWith("<")) {
                        message.addExtra(ChatUtil.combine(ChatColor.RED, String.format("%s ", argument)));
                    } else {
                        message.addExtra(ChatUtil.combine(ChatColor.GRAY, String.format("%s ", argument)));
                    }
                }
            }

            message.addExtra(ChatUtil.combine(ChatColor.GRAY, "- ", ChatColor.BLUE, f.getPurpose()));
            sender.sendMessage(message);
        }
    }
}
