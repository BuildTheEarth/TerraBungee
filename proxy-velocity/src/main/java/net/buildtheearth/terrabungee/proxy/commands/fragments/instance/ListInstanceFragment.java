/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - ListInstanceFragment.java
 */

package net.buildtheearth.terrabungee.proxy.commands.fragments.instance;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.commands.ICommandFragment;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class ListInstanceFragment implements ICommandFragment {
    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource sender = invocation.source();

        TextComponent list = ChatUtil.titleAndCombine(NamedTextColor.RED, "Instances: ");
        boolean first = true;
        for (Instance i : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
            if (first) {
                first = false;
            } else {
                list = list.append(ChatUtil.combine(NamedTextColor.GRAY, ", "));
            }

            list = list.append(
                ChatUtil.combine((i.getInstanceType() == Instance.InstanceType.STATIC ? NamedTextColor.GOLD : NamedTextColor.GREEN), i.getId())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tba instance info %s", i.getId())))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, ChatUtil.combine("Get info about " + i.getId())))
            );
        }

        sender.sendMessage(list);
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
