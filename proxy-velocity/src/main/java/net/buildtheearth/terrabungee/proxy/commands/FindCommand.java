package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.players.PlayerHandler;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.proxy.util.ProxyUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FindCommand extends Command {
    public FindCommand() {
        super("find", "");
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.NO_PERMISSION);
            return;
        }
        if(args.length < 1) {
            sender.sendMessage(ChatUtil.USAGE("/find <username>"));
            return;
        }
        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(args[0]);
        playerFuture.thenAccept(tbPlayer -> {
           if(tbPlayer == null) {
               sender.sendMessage(ChatUtil.PLAYER_NEVER_JOINED(args[0]));
               return;
           }
           if(!tbPlayer.isOnline()) {
               sender.sendMessage(ChatUtil.PLAYER_NOT_ONLINE(tbPlayer.getName()));
               return;
           }
           sender.sendMessage(ChatUtil.titleAndCombine(
                   NamedTextColor.YELLOW, tbPlayer.getName(),
                   NamedTextColor.GRAY, " is currently on proxy ",
                   NamedTextColor.BLUE, tbPlayer.getProxy(),
                   NamedTextColor.GRAY, " on server ",
                   NamedTextColor.RED, tbPlayer.getServer())
           );
        });
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length < 1)
            return CompletableFuture.completedFuture(List.of());

        return CompletableFuture.supplyAsync(() -> {
            List<String> completion = new ArrayList<>();
            ProxyUtil.copyPartialMatches(args[0], PlayerHandler.getInstance().getOnlinePlayerNames(), completion);

            return completion;

        });
    }
}
