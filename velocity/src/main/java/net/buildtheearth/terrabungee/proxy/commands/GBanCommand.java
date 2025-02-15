package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.proxy.util.DateUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CBanPlayerPacket;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Noah Husby
 */
public class GBanCommand extends Command {
    public GBanCommand() {
        super("gban", "");
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.NO_PERMISSION);
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "Usage: /gban <player> <length> <reason>"));
            return;
        }
        String player = args[0];
        String lengthString = args[1];
        long length;
        try {
            if (lengthString.equals("0")) {
                length = 0;
            } else {
                length = DateUtil.parseDateDiff(lengthString, true, true);
            }
        } catch (Exception ignored) {
            sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.BLUE, lengthString, NamedTextColor.GRAY, " is not a valid length! Enter the amount of time for the ban (Ex: 1d12h), or enter ", NamedTextColor.YELLOW, "0 ", NamedTextColor.GRAY, "for a permanent ban."));
            return;
        }
        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(player);
        playerFuture.thenAccept(tbPlayer -> {
            if (tbPlayer == null) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.YELLOW, player, NamedTextColor.GRAY, " has never joined the network!"));
                return;
            }
            StringBuilder reason = new StringBuilder();
            for (String r : Arrays.copyOfRange(args, 2, args.length)) {
                reason.append(r).append(" ");
            }
            reason = new StringBuilder(reason.toString().trim());
            CompletableFuture<Response> banFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CBanPlayerPacket((sender instanceof Player) ? ((Player) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), tbPlayer.getUniqueID(), length, reason.toString()));
            String finalReason = reason.toString();
            banFuture.thenAccept(response -> {
                if (response.getCode() == Response.ResponseCode.ERROR) {
                    sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.YELLOW, tbPlayer.getName(), NamedTextColor.GRAY, " was already banned!"));
                } else if (response.getCode() == Response.ResponseCode.SUCCESS) {
                    sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GRAY, "Successfully banned ", NamedTextColor.YELLOW, tbPlayer.getName(), lengthString.equals("0") ? " permanently" : " for " + DateUtil.getExpandedTimeMessage(length), NamedTextColor.GRAY, " for ", NamedTextColor.BLUE, finalReason));
                } else {
                    sender.sendMessage(ChatUtil.NO_CONTROLLER_CONTACT);
                }
            });
        });
    }
}
