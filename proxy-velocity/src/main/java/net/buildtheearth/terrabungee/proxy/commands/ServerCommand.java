package net.buildtheearth.terrabungee.proxy.commands;

import com.google.common.collect.Lists;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.proxy.util.ProxyUtil;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ServerCommand extends Command {

    public ServerCommand() {
        super("server",  "");
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if(!(source instanceof Player)) {
            source.sendMessage(
                    ChatUtil.PLAYER_ONLY
            );
            return;
        }
        if(args.length < 1) {
            TextComponent list = ChatUtil.titleAndCombineError("Servers: ");

            boolean first = true;
            for (Instance i : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
                if (first) {
                    first = false;
                } else {
                    list = list.append(Component.text(", ", NamedTextColor.GRAY));
                }

                TextComponent t = Component.text(i.getId(), (i.getInstanceType() == Instance.InstanceType.STATIC ?
                        NamedTextColor.GOLD : NamedTextColor.GREEN))
                        .clickEvent(ClickEvent.runCommand(String.format("/server %s", i.getId())))
                        .hoverEvent(Component.text("Connect to " + i.getId()));

                list = list.append(t);
            }

            source.sendMessage(list);
        } else {
            String server = args[0];
            Optional<RegisteredServer> serverInfo = TerraBungeeProxy.getServer().getServer(server);

            if(serverInfo.isEmpty())
                source.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.YELLOW, server, NamedTextColor.RED, " does not exist!"));


            //Literally fire and forget
            if(serverInfo.isEmpty()) return;
            ((Player) source).createConnectionRequest(serverInfo.get()).fireAndForget();
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("terrabungee.admin");
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length < 1)
            return CompletableFuture.completedFuture(List.of());

        return CompletableFuture.supplyAsync(() -> {
            List<String> completion = new ArrayList<>();
            List<String> servers = Lists.newArrayList();

            for(Instance instance : TerraBungeeProxy.getInstance().getTerraBungee().getInstanceManager().getInstances()) {
                servers.add(instance.getId());
            }

            ProxyUtil.copyPartialMatches(args[0], servers, completion);

            return completion;
        });
    }
}
