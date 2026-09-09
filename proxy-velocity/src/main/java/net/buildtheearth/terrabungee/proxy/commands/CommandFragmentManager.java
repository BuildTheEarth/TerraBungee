/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - CommandFragmentManager.java
 */

package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandFragmentManager extends Command {

    public CommandFragmentManager(String name, String node) {
        super(name, node);
    }

    public CommandFragmentManager(String name, String node, String[] alias) {
        super(name, node, alias);
    }

    private List<ICommandFragment> commandFragments = new ArrayList<>();
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

    protected void executeFragment(CommandSource sender, String[] args) {
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

    private void displayCommands(CommandSource sender, String[] args) {
        sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GRAY, title + ":"));
        for (ICommandFragment f : commandFragments) {

            Component message = Component.text()
            .append(ChatUtil.combine(NamedTextColor.YELLOW, commandBase))
            .append(ChatUtil.combine(NamedTextColor.GREEN, String.format("%s ", f.getName())))
            .build();

            if (f.getArguments() != null) {
                for (int x = 0; x < f.getArguments().length; x++) {
                    String argument = f.getArguments()[x];
                    if (argument.startsWith("<")) {
                        message = message.append(ChatUtil.combine(NamedTextColor.RED, String.format("%s ", argument)));
                    } else {
                        message = message.append(ChatUtil.combine(NamedTextColor.GRAY, String.format("%s ", argument)));
                    }
                }
            }

            message = message.append(ChatUtil.combine(NamedTextColor.GRAY, "- ", NamedTextColor.BLUE, f.getPurpose()));
            sender.sendMessage(message);
        }

    }
}
