/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - ListInstanceFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ListInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent list = ChatUtil.titleAndCombine(ChatColor.RED, "Instances: ");
        boolean first = true;
        for (Instance i : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
            if (first) {
                first = false;
            } else {
                list.addExtra(ChatUtil.combine(ChatColor.GRAY, ", "));
            }

            TextComponent t = ChatUtil.combine((i.getInstanceType() == Instance.InstanceType.STATIC ?
                    ChatColor.GOLD : ChatColor.GREEN), i.getId());
            t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/tba instance info %s", i.getId())));
            t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Get info about " + i.getId()).create()));
            list.addExtra(t);
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
