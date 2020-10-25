/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - AddInstanceFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.api.network.Response;
import com.noahhusby.terrabungee.api.network.S2C.S2CAddStaticInstancePacket;
import com.noahhusby.terrabungee.api.network.S2C.S2CKeepAlivePacket;
import com.noahhusby.terrabungee.api.network.S2C.S2CResponsePacket;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxyMain;
import com.noahhusby.terrabungee.proxy.chat.ChatHelper;
import com.noahhusby.terrabungee.proxy.chat.TextElement;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class AddInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Usage: /tba instance add <id> <address>", ChatColor.RED)));
            return;
        }
        TerraBungeeProxyMain.tb.getNetworkManager().send(new S2CResponsePacket(new S2CAddStaticInstancePacket(args[0], args[1]),
                (responseCode, jsonObject) -> {
                    if(responseCode == Response.ResponseCode.TIMED_OUT) {
                        sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("The controller was unable to be contacted. Please check the connection and try again.", ChatColor.RED)));
                        return;
                    }

                    if(responseCode == Response.ResponseCode.ERROR) {
                        sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Static instance ", ChatColor.GRAY),
                                new TextElement(args[0].toLowerCase(), ChatColor.BLUE), new TextElement(" already exists!", ChatColor.RED)));
                        return;
                    }

                    sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Successfully created static instance ", ChatColor.GREEN),
                            new TextElement(args[0].toLowerCase(), ChatColor.BLUE)));
                }));
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getPurpose() {
        return "Add a static instance";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"<id>", "<address>"};
    }
}
