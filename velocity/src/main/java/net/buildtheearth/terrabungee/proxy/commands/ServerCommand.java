package net.buildtheearth.terrabungee.proxy.commands;

import com.google.common.collect.Lists;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.util.ProxyUtil;
import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.proxy.util.VelocityChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ServerCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if(!(source instanceof Player)) {
            source.sendMessage(
                    VelocityChatUtil.PLAYER_ONLY
            );
            return;
        }
        if(args.length < 1) {
            TextComponent.Builder list = Component.text()
                    .append(VelocityChatUtil.PREFIX)
                    .append(Component.text("Servers: ", NamedTextColor.RED));

            boolean first = true;
            for (Instance i : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
                if (first) {
                    first = false;
                } else {
                    list.append(Component.text(", ", NamedTextColor.GRAY));
                }

                TextComponent.Builder t = Component.text(i.getId(), (i.getInstanceType() == Instance.InstanceType.STATIC ?
                        NamedTextColor.GOLD : NamedTextColor.GREEN))
                        .toBuilder();
                t.clickEvent(ClickEvent.runCommand(String.format("/server %s", i.getId())));
                t.hoverEvent(Component.text("Connect to " + i.getId()));

                list.append(t.build());
            }

            source.sendMessage(list);
        } else {
            String server = args[0];
            Optional<RegisteredServer> serverInfo = TerraBungeeProxy.getServer().getServer(server);
            if(serverInfo.isEmpty()) {
                source.sendMessage(
                        Component.text()
                                .append(VelocityChatUtil.PREFIX)
                                .append(Component.text(server, NamedTextColor.YELLOW))
                                .append(Component.text(" does not exist!", NamedTextColor.RED))
                );
            }

            //Literally fire and forget
            ((Player) source).createConnectionRequest(serverInfo.get()).fireAndForget();
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        if (invocation.source().hasPermission("terrabungee.admin")) {
            return true;
        } else {
            invocation.source().sendMessage(
                    VelocityChatUtil.NO_PERMISSION
            );
            return false;
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {

        if (invocation.arguments() == null || invocation.arguments().length < 1) {
            return CompletableFuture.completedFuture(List.of());
        }

        return CompletableFuture.supplyAsync(() -> {
            List<String> completion = new ArrayList<>();
            List<String> servers = Lists.newArrayList();

            for(Instance instance : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
                servers.add(instance.getId());
            }

            ProxyUtil.copyPartialMatches(invocation.arguments()[0], servers, completion);

            return completion;

        });

    }


}
