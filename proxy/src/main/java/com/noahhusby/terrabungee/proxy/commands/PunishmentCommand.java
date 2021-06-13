package com.noahhusby.terrabungee.proxy.commands;

import com.google.common.reflect.TypeToken;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import com.noahhusby.terrabungee.proxy.util.ProxyUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrievePunishmentsPacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Noah Husby
 */
public class PunishmentCommand extends Command {
    public PunishmentCommand() {
        super("punishment", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasAdmin(sender)) {
            sender.sendMessage(ChatUtil.getNoPermission());
            return;
        }
        if(args.length < 1) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment <get | edit>"));
            return;
        }
        if(args[0].equalsIgnoreCase("get")) {
            executeGet(sender, args);
        } else if(args[0].equalsIgnoreCase("edit")) {
            executeEdit(sender, args);
        } else {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment <get | edit>"));
            return;
        }
    }

    private void executeGet(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment get <player>"));
            return;
        }
        String playerName = args[1];
        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(playerName);
        playerFuture.thenAccept(tbPlayer -> {
            if(tbPlayer == null) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, playerName, ChatColor.GRAY, " has never joined the network!"));
                return;
            }
            CompletableFuture<Response> punishmentFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrievePunishmentsPacket(tbPlayer.getUniqueID()));
            punishmentFuture.thenAccept(response -> {
                if(response.getCode() == Response.ResponseCode.ERROR) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, playerName, ChatColor.GRAY, " has no punishments on record."));
                    return;
                }
                Type punishmentListType = new TypeToken<ArrayList<Punishment>>(){}.getType();
                List<Punishment> punishments = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishments"), punishmentListType);
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Punishments for ", ChatColor.YELLOW, tbPlayer.getName(), ChatColor.GRAY, ":"));
                for(Punishment punishment : punishments) {
                    BaseComponent punishmentMessage = ChatUtil.combine(ChatColor.RED, "#", punishment.getId(), ChatColor.GRAY, " - ", ChatColor.BLUE, punishment.getType().name(), " ");
                    TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(punishment.getStaff()).thenAccept(staffPlayer -> {
                        BaseComponent[] hoverMessage = new ComponentBuilder("")
                                .append(ChatUtil.combine(ChatColor.GRAY, "Reason: ", ChatColor.WHITE, punishment.getReason(), "\n"))
                                .append(ChatUtil.combine(ChatColor.GRAY, "Start: ", ChatColor.WHITE, ProxyUtil.toReadableTime(punishment.getStart()), "\n"))
                                .append(ChatUtil.combine(ChatColor.GRAY, "End: ", ChatColor.WHITE, punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(punishment.getEnd()), "\n"))
                                .append(ChatUtil.combine(ChatColor.GRAY, "Staff: ", ChatColor.WHITE, staffPlayer == null ? "Unknown" : staffPlayer.getName()))
                                .create();
                        punishmentMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
                        TextComponent interaction = new TextComponent(ChatColor.YELLOW + "[*]");
                        interaction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "https://giant.gfycat.com/JitteryTerrificChimpanzee.webm"));
                        interaction.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Inspect the punishment").create()));
                        punishmentMessage.addExtra(interaction);
                        if(punishment.isActive()) {
                            TextComponent active = new TextComponent(" " + ChatColor.GREEN + "\u2713");
                            active.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Punishment is active").create()));
                            punishmentMessage.addExtra(active);
                        }
                        sender.sendMessage(punishmentMessage);
                    });
                }
            });
        });
    }

    private void executeEdit(CommandSender sender, String[] args) {

    }

    private void executeInspect(CommandSender sender, String[] args) {

    }
}
