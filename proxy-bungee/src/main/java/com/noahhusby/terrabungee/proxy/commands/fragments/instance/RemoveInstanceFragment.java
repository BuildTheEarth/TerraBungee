/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - RemoveInstanceFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRemoveStaticInstancePacket;
import net.buildtheearth.terrabungee.common.network.Response;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.concurrent.CompletableFuture;

public class RemoveInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /tba instance remove <id>"));
            return;
        }

        CompletableFuture<Response> response = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRemoveStaticInstancePacket(args[0]));
        response.thenAccept(r -> {
            if (r.getCode() == Response.ResponseCode.TIMED_OUT) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "The controller was unable to be contacted. Please check the connection and try again."));
                return;
            }

            if (r.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Could not find static instance ", ChatColor.RED, args[0].toLowerCase(), ChatColor.GRAY, "!"));
                return;
            }

            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GREEN, "Successfully removed static instance ",
                    ChatColor.RED, args[0].toLowerCase()));
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
        return new String[]{ "<id>" };
    }

}
