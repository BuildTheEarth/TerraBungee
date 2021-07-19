package com.noahhusby.terrabungee.proxy.commands;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import com.noahhusby.terrabungee.proxy.util.ChatUtil;
import com.noahhusby.terrabungee.proxy.util.ProxyUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CEditPunishmentPacket;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrievePunishmentPacket;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrievePunishmentsPacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.common.players.PunishmentEditAction;
import net.buildtheearth.terrabungee.common.players.PunishmentHistory;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
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
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment <get | edit | inspect>"));
            return;
        }
        if(args[0].equalsIgnoreCase("get")) {
            executeGet(sender, args);
        } else if(args[0].equalsIgnoreCase("edit")) {
            executeEdit(sender, args);
        } else if(args[0].equalsIgnoreCase("inspect")) {
            executeInspect(sender, args);
        } else {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment <get | edit | inspect>"));
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
                System.out.println("1");
                Type punishmentListType = new TypeToken<ArrayList<Punishment>>(){}.getType();
                System.out.println("2");
                List<Punishment> punishments = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishments"), punishmentListType);
                System.out.println("3");
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
                        interaction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishment inspect " + punishment.getId()));
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
        if(args.length < 3) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment edit <punishment id> <deactivate | end | reason>"));
            return;
        }
        try {
            Integer.parseInt(args[1]);
        } catch (Exception ignored) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, args[1], ChatColor.GRAY, " is not a valid number"));
            return;
        }
        String command = args[2].toLowerCase(Locale.ROOT);
        UUID staff = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
        CompletableFuture<Response> punishmentFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrievePunishmentPacket(args[1]));
        punishmentFuture.thenAccept(response -> {
            if(response.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "That punishment does not exist"));
                return;
            }
            Punishment punishment = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishment"), Punishment.class);
            if(punishment.getType() != Punishment.Type.BAN) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "You cannot edit this type of punishment!"));
                return;
            }
            if(command.equalsIgnoreCase("deactivate")) {
                if(!punishment.isActive()) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "That punishment is already inactive!"));
                    return;
                }
                edit(staff, args[1], PunishmentEditAction.DEACTIVATE, new JsonObject()).thenAccept(a -> {
                    if(a) {
                        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GREEN, "Successfully deactivated the punishment"));
                    } else {
                        sender.sendMessage(ChatUtil.getNoContact());
                    }
                });
            } else if(command.equalsIgnoreCase("end")) {
                if(args.length < 4) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment edit <punishment id> end <days>"));
                    return;
                }
                JsonObject data = new JsonObject();
                String lengthString = args[3];
                try {
                    int length = Integer.parseInt(lengthString);
                    if(length < 0) {
                        throw new Exception();
                    }
                } catch (Exception ignored) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.BLUE, lengthString, ChatColor.GRAY, " is not a valid length! Enter the amount of days for the ban, or enter ", ChatColor.YELLOW, "0 ", ChatColor.GRAY, "for a permanent ban."));
                    return;
                }
                data.addProperty("days", lengthString);
                edit(staff, args[1], PunishmentEditAction.END, data).thenAccept(a -> {
                    if(a) {
                        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Successfully changed the end date from ", ChatColor.YELLOW,  punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(punishment.getEnd()), ChatColor.GRAY, " to ",
                                ChatColor.YELLOW, ProxyUtil.toReadableTime(punishment.getStart().plusDays(Integer.parseInt(lengthString)))));
                    } else {
                        sender.sendMessage(ChatUtil.getNoContact());
                    }
                });
            } else if(command.equalsIgnoreCase("reason")) {
                if(args.length < 4) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment edit <punishment id> reason <reason>"));
                    return;
                }
                JsonObject data = new JsonObject();
                StringBuilder reason = new StringBuilder();
                for(String r : Arrays.copyOfRange(args, 3, args.length)) {
                    reason.append(r).append(" ");
                }
                reason = new StringBuilder(reason.toString().trim());
                data.addProperty("reason", reason.toString());
                StringBuilder finalReason = reason;
                edit(staff, args[1], PunishmentEditAction.REASON, data).thenAccept(a -> {
                    if(a) {
                        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Successfully changed the reason from ", ChatColor.YELLOW, punishment.getReason(), ChatColor.GRAY, " to ",
                                ChatColor.YELLOW, finalReason.toString()));
                    } else {
                        sender.sendMessage(ChatUtil.getNoContact());
                    }
                });
            } else {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment edit <punishment id> <deactivate | end | reason>"));
            }
        });
    }

    private void executeInspect(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment inspect <punishment id>"));
            return;
        }
        try {
            Integer.parseInt(args[1]);
        } catch (Exception ignored) {
            sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.YELLOW, args[1], ChatColor.GRAY, " is not a valid number"));
            return;
        }
        CompletableFuture<Response> punishmentFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrievePunishmentPacket(args[1]));
        punishmentFuture.thenAccept(response -> {
            if(response.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "That punishment does not exist"));
                return;
            }
            Punishment punishment = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishment"), Punishment.class);
            BaseComponent inspectMessage = ChatUtil.combine(ChatColor.YELLOW, "ID: ", ChatColor.WHITE, punishment.getId(), "\n");
            inspectMessage.addExtra(ChatUtil.combine(ChatColor.YELLOW, "Player: ", ChatColor.WHITE, response.getData().get("playerName").getAsString(), "\n"));
            inspectMessage.addExtra(ChatUtil.combine(ChatColor.YELLOW, "Type: ", ChatColor.WHITE, punishment.getType().name(), "\n\n"));

            inspectMessage.addExtra(ChatUtil.combine(ChatColor.YELLOW, "Reason: ", ChatColor.WHITE, punishment.getReason(), "\n\n"));

            inspectMessage.addExtra(ChatUtil.combine(ChatColor.BLUE, "Staff: ", ChatColor.WHITE, response.getData().get("staffName").getAsString(), "\n"));
            inspectMessage.addExtra(ChatUtil.combine(ChatColor.BLUE, "Start: ", ChatColor.WHITE, ProxyUtil.toReadableTime(punishment.getStart()), "\n"));
            inspectMessage.addExtra(ChatUtil.combine(ChatColor.BLUE, "End: ", ChatColor.WHITE, punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(punishment.getEnd()), "\n\n"));

            inspectMessage.addExtra(ChatUtil.combine(ChatColor.RED, "History:\n"));
            for(PunishmentHistory history : punishment.getHistory()) {
                inspectMessage.addExtra(ChatUtil.combine(ChatColor.GRAY, "[", ProxyUtil.toReadableTime(history.getDate()), "] ", ChatColor.DARK_GRAY, "> ", ChatColor.WHITE, history.getType().name(), "\n"));
            }
            ChatUtil.sendMessageBox(sender, ChatColor.YELLOW + "" + ChatColor.BOLD + "Punishment Report", inspectMessage);
        });
    }

    private CompletableFuture<Boolean> edit(UUID staff, String id, PunishmentEditAction action, JsonObject data) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        CompletableFuture<Response> editFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CEditPunishmentPacket(staff, id, action, data));
        editFuture.thenAccept(response -> {
            if(response.getCode() == Response.ResponseCode.SUCCESS) {
                future.complete(true);
            } else {
                future.complete(false);
            }
        });
        return future;
    }
}
