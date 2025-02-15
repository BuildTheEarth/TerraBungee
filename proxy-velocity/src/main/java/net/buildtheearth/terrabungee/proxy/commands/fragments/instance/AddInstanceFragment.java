/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - AddInstanceFragment.java
 */

package net.buildtheearth.terrabungee.proxy.commands.fragments.instance;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.commands.ICommandFragment;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CAddStaticInstancePacket;
import net.buildtheearth.terrabungee.common.network.Response;
import net.kyori.adventure.text.format.NamedTextColor;


import java.util.concurrent.CompletableFuture;

public class AddInstanceFragment implements ICommandFragment {

    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 2) {
            sender.sendMessage(ChatUtil.USAGE("/tba instance add <id> <address>"));
            return;
        }

        CompletableFuture<Response> response = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CAddStaticInstancePacket(args[0], args[1]));
        response.thenAccept(r -> {
            if (r.getCode() == Response.ResponseCode.TIMED_OUT) {
                sender.sendMessage(ChatUtil.NO_CONTROLLER_CONTACT);
                return;
            }

            if (r.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GRAY, "Static instance ", NamedTextColor.BLUE,
                        args[0].toLowerCase(), NamedTextColor.RED, " already exists!"));
                return;
            }

            sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GREEN, "Successfully created static instance ",
                    NamedTextColor.BLUE, args[0].toLowerCase()));
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
        return new String[]{ "<id>", "<address>" };
    }
}
