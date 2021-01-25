/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - AddInstanceFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.api.network.Response;
import com.noahhusby.terrabungee.api.network.S2C.S2CAddStaticInstancePacket;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.concurrent.CompletableFuture;

public class AddInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /tba instance add <id> <address>"));
            return;
        }

        CompletableFuture<Response> response = TerraBungeeProxy.tb.getNetworkManager().send(new S2CAddStaticInstancePacket(args[0], args[1]));
        response.thenAccept(r -> {
            if(r.getCode() == Response.ResponseCode.TIMED_OUT) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "The controller was unable to be contacted. Please check the connection and try again."));
                return;
            }

            if(r.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Static instance ", ChatColor.BLUE,
                        args[0].toLowerCase(), ChatColor.RED, " already exists!"));
                return;
            }

            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GREEN, "Successfully created static instance ",
                    ChatColor.BLUE, args[0].toLowerCase()));
        });
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
