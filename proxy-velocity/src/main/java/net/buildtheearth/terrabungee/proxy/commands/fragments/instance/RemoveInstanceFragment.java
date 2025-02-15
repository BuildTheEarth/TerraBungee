/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - RemoveInstanceFragment.java
 */

package net.buildtheearth.terrabungee.proxy.commands.fragments.instance;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.commands.ICommandFragment;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRemoveStaticInstancePacket;
import net.buildtheearth.terrabungee.common.network.Response;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.CompletableFuture;

public class RemoveInstanceFragment implements ICommandFragment {
    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 1) {
            sender.sendMessage(ChatUtil.USAGE("/tba instance remove <id>"));
            return;
        }

        CompletableFuture<Response> response = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRemoveStaticInstancePacket(args[0]));
        response.thenAccept(r -> {
            if (r.getCode() == Response.ResponseCode.TIMED_OUT) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "The controller was unable to be contacted. Please check the connection and try again."));
                return;
            }

            if (r.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GRAY, "Could not find static instance ", NamedTextColor.RED, args[0].toLowerCase(), NamedTextColor.GRAY, "!"));
                return;
            }

            sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GREEN, "Successfully removed static instance ",
                    NamedTextColor.RED, args[0].toLowerCase()));
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
