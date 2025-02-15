package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CKickPlayerPacket;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Noah Husby
 */
public class GKickCommand extends Command {
    public GKickCommand() {
        super("gkick", "");
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.NO_PERMISSION);
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "Usage: /gkick <player> <reason>"));
            return;
        }
        String player = args[0];

        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(player);
        playerFuture.thenAccept(tbPlayer -> {
            if (tbPlayer == null) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.YELLOW, player, NamedTextColor.GRAY, " has never joined the network!"));
                return;
            }
            StringBuilder reason = new StringBuilder();
            for (String r : Arrays.copyOfRange(args, 1, args.length)) {
                reason.append(r).append(" ");
            }
            reason = new StringBuilder(reason.toString().trim());
            CompletableFuture<Response> kickFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CKickPlayerPacket((sender instanceof Player) ? ((Player) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), tbPlayer.getUniqueID(), reason.toString()));
            String finalReason = reason.toString();
            kickFuture.thenAccept(response -> {
                if (response.getCode() == Response.ResponseCode.ERROR) {
                    sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.YELLOW, tbPlayer.getName(), NamedTextColor.GRAY, " is not online!"));
                } else if (response.getCode() == Response.ResponseCode.SUCCESS) {
                    sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GRAY, "Successfully kicked ", NamedTextColor.YELLOW, tbPlayer.getName(), NamedTextColor.GRAY, " for ", NamedTextColor.BLUE, finalReason));
                } else {
                    sender.sendMessage(ChatUtil.NO_CONTROLLER_CONTACT);
                }
            });
        });
    }
}
