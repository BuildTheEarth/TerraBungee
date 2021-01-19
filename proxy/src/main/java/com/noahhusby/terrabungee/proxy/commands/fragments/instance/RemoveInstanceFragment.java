/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - RemoveInstanceFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.api.network.Response;
import com.noahhusby.terrabungee.api.network.S2C.S2CRemoveStaticInstancePacket;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxyMain;
import com.noahhusby.terrabungee.proxy.chat.ChatHelper;
import com.noahhusby.terrabungee.proxy.chat.TextElement;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.concurrent.CompletableFuture;

public class RemoveInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Usage: /tba instance remove <id>", ChatColor.RED)));
            return;
        }

        CompletableFuture<Response> response = TerraBungeeProxyMain.tb.getNetworkManager().send(new S2CRemoveStaticInstancePacket(args[0]));
        response.thenAccept(r -> {
            if(r.getCode() == Response.ResponseCode.TIMED_OUT) {
                sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("The controller was unable to be contacted. Please check the connection and try again.", ChatColor.RED)));
                return;
            }

            if(r.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Could not find static instance ", ChatColor.GRAY),
                        new TextElement(args[0].toLowerCase(), ChatColor.BLUE)));
                return;
            }

            sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("Successfully removed static instance ", ChatColor.GREEN),
                    new TextElement(args[0].toLowerCase(), ChatColor.BLUE)));
        });

    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getPurpose() {
        return "Remove a static instance";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"<id>"};
    }

}
