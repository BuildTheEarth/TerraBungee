/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - FragmentManager.java
 */

package net.buildtheearth.terrabungee.proxy.commands;
import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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

    protected void executeFragment(SimpleCommand.Invocation invocation) {
        executeFragment(invocation, 0);
    }

    protected void executeFragment(SimpleCommand.Invocation invocation, int index) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

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
                        f.execute(invocation);
                        return;
                    }
                }
                displayCommands(sender);
            } else {
                for (ICommandFragment f : commandFragments) {
                    if (f.getName().equals(args[index].toLowerCase())) {
                        f.execute(invocation);
                        return;
                    }
                }
                displayCommands(sender);
            }
        }
    }

    private void displayCommands(CommandSource sender) {
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
