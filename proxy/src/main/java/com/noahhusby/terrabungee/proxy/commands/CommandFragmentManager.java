/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - CommandFragmentManager.java
 */

package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandFragmentManager extends Command {

    private List<ICommandFragment> commandFragments = new ArrayList<>();
    private String title = "";
    private String commandBase = "";
    public CommandFragmentManager(String name, String node) {
        super(name, node);
    }
    public CommandFragmentManager(String name, String node, String[] alias) {
        super(name, node, alias);
    }

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
        if (args.length != 0) {
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
        }
        displayCommands(sender, args);
    }

    private void displayCommands(CommandSender sender, String[] args) {
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
