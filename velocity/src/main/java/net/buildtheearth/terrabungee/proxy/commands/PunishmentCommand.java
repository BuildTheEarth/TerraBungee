package net.buildtheearth.terrabungee.proxy.commands;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.util.ChatUtil;
import net.buildtheearth.terrabungee.proxy.util.DateUtil;
import net.buildtheearth.terrabungee.proxy.util.ProxyUtil;
import net.buildtheearth.terrabungee.client.network.S2C.S2CEditPunishmentPacket;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrievePunishmentPacket;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrievePunishmentsPacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.common.players.PunishmentEditAction;
import net.buildtheearth.terrabungee.common.players.PunishmentHistory;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Noah Husby & XboxBedrock
 */
public class PunishmentCommand extends Command {

    public PunishmentCommand() {
        super("punishment", "");
    }


    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 1) {
            sender.sendMessage(ChatUtil.USAGE("/punishment <get | edit | inspect>"));
            return;
        }

        if (args[0].equalsIgnoreCase("get"))
            executeGet(invocation);
        else if (args[0].equalsIgnoreCase("edit"))
            executeEdit(invocation);
        else if (args[0].equalsIgnoreCase("inspect"))
            executeInspect(invocation);
        else
            sender.sendMessage(ChatUtil.USAGE("/punishment <get | edit | inspect>"));
    }

    private void executeGet(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 2) {
            sender.sendMessage(ChatUtil.USAGE("/punishment get <player>"));
            return;
        }

        String playerName = args[1];
        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(playerName);
        playerFuture.thenAccept(tbPlayer -> {
            if (tbPlayer == null) {
                sender.sendMessage(ChatUtil.PLAYER_NEVER_JOINED(playerName));
                return;
            }
            CompletableFuture<Response> punishmentFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrievePunishmentsPacket(tbPlayer.getUniqueID()));
            punishmentFuture.thenAccept(response -> {
                if (response.getCode() == Response.ResponseCode.ERROR) {
                    sender.sendMessage(ChatUtil.PLAYER_HAS_NO_PUNISHMENTS(playerName));
                    return;
                }
                Type punishmentListType = new TypeToken<ArrayList<Punishment>>() {}.getType();
                List<Punishment> punishments = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishments"), punishmentListType);
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GRAY, "Punishments for ", NamedTextColor.YELLOW, tbPlayer.getName(), NamedTextColor.GRAY, ":"));

                for (Punishment punishment : punishments) {
                    TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(punishment.getStaff()).thenAccept(staffPlayer -> {
                        TextComponent punishmentMessage = ChatUtil.titleAndCombine(
                                NamedTextColor.RED, "#" + punishment.getId(),
                                NamedTextColor.GRAY, " - ",
                                NamedTextColor.BLUE, punishment.getType().name(),
                                NamedTextColor.GRAY, " "
                        );

                        TextComponent hoverMessage = ChatUtil.titleAndCombine(
                            NamedTextColor.GRAY, "Reason: ",
                            NamedTextColor.WHITE, punishment.getReason(),
                            NamedTextColor.GRAY, "\nStart: ",
                            NamedTextColor.WHITE, ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getStart())),
                            NamedTextColor.GRAY, "\nEnd: ",
                            NamedTextColor.WHITE, punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getEnd())),
                            NamedTextColor.GRAY, "\nStaff: ",
                            NamedTextColor.WHITE, staffPlayer == null ? "Unknown" : staffPlayer.getName()
                        );

                        punishmentMessage = punishmentMessage.hoverEvent(HoverEvent.showText(hoverMessage));
                        punishmentMessage = punishmentMessage.append(
                                Component.text("[*]", NamedTextColor.YELLOW)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/punishment inspect " + punishment.getId()))
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, ChatUtil.combine("Inspect the punishment")))
                        );

                        if (punishment.isActive()) {
                            punishmentMessage = punishmentMessage.append(
                                ChatUtil.combine(NamedTextColor.GREEN + "✓")
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, ChatUtil.combine(NamedTextColor.GREEN + "Punishment is active")))
                            );
                        }
                        sender.sendMessage(punishmentMessage);
                    });
                }
            });
        });
    }

    private void executeEdit(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 3) {
            sender.sendMessage(ChatUtil.USAGE("/punishment edit <punishment id> <deactivate | end | reason>"));
            return;
        }

        // Check if argument is a number
        try {
            Integer.parseInt(args[1]);
        } catch (Exception ignored) {
            sender.sendMessage(ChatUtil.NOT_A_NUMBER(args[1]));
            return;
        }

        String command = args[2].toLowerCase(Locale.ROOT);
        UUID staff = (sender instanceof Player) ? ((Player) sender).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
        CompletableFuture<Response> punishmentFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrievePunishmentPacket(args[1]));
        punishmentFuture.thenAccept(response -> {
            if (response.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "That punishment does not exist"));
                return;
            }
            Punishment punishment = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishment"), Punishment.class);
            if (punishment.getType() == Punishment.Type.KICK) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "You cannot edit this type of punishment!"));
                return;
            }
            if (command.equalsIgnoreCase("deactivate")) {
                if (!punishment.isActive()) {
                    sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GRAY, "That punishment is already inactive!"));
                    return;
                }
                edit(staff, args[1], PunishmentEditAction.DEACTIVATE, new JsonObject()).thenAccept(a -> {
                    if(a) sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.GREEN, "Successfully deactivated the punishment"));
                    else sender.sendMessage(ChatUtil.NO_CONTROLLER_CONTACT);
                });
            } else if (command.equalsIgnoreCase("end")) {
                if (args.length < 4) {
                    sender.sendMessage(ChatUtil.USAGE("/punishment edit <punishment id> end <days>"));
                    return;
                }
                JsonObject data = new JsonObject();
                String lengthString = args[3];
                long length;
                try {
                    if (lengthString.equals("0")) length = 0;
                    else length = DateUtil.parseDateDiff(lengthString, true, true);

                } catch (Exception ignored) {
                    sender.sendMessage(ChatUtil.titleAndCombine(
                            NamedTextColor.BLUE, lengthString, NamedTextColor.GRAY, " is not a valid length! Enter the amount of time for the punishment (Ex: 1d12h), or enter ",
                            NamedTextColor.YELLOW, "0 ",
                            NamedTextColor.GRAY, "for a permanent punishment."
                    ));
                    return;
                }
                data.addProperty("length", length);
                edit(staff, args[1], PunishmentEditAction.END, data).thenAccept(a -> {
                    if (a) {
                        sender.sendMessage(ChatUtil.titleAndCombine(
                                NamedTextColor.GRAY, "Successfully changed the end date from ",
                                NamedTextColor.YELLOW, punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getEnd())),
                                NamedTextColor.GRAY, " to ",
                                NamedTextColor.YELLOW, ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getStart()).plusSeconds(length == 0 ? 0 : length / 1000))
                        ));
                    } else
                        sender.sendMessage(ChatUtil.NO_CONTROLLER_CONTACT);

                });
            } else if (command.equalsIgnoreCase("reason")) {
                if (args.length < 4) {
                    sender.sendMessage(ChatUtil.USAGE("/punishment edit <punishment id> reason <reason>"));
                    return;
                }
                JsonObject data = new JsonObject();
                StringBuilder reason = new StringBuilder();
                for (String r : Arrays.copyOfRange(args, 3, args.length)) {
                    reason.append(r).append(" ");
                }
                reason = new StringBuilder(reason.toString().trim());
                data.addProperty("reason", reason.toString());
                StringBuilder finalReason = reason;
                edit(staff, args[1], PunishmentEditAction.REASON, data).thenAccept(a -> {
                    if (a) {
                        sender.sendMessage(ChatUtil.titleAndCombine(
                                NamedTextColor.GRAY, "Successfully changed the reason from ",
                                NamedTextColor.YELLOW, punishment.getReason(),
                                NamedTextColor.GRAY, " to ",
                                NamedTextColor.YELLOW, finalReason.toString()));
                    } else
                        sender.sendMessage(ChatUtil.NO_CONTROLLER_CONTACT);
                });
            } else {
                sender.sendMessage(ChatUtil.USAGE("/punishment edit <punishment id> <deactivate | end | reason>"));
            }
        });
    }

    private void executeInspect(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 2) {
            sender.sendMessage(ChatUtil.USAGE("/punishment inspect <punishment id>"));
            return;
        }
        try {
            Integer.parseInt(args[1]);
        } catch (Exception ignored) {
            sender.sendMessage(ChatUtil.NOT_A_NUMBER(args[1]));
            return;
        }
        CompletableFuture<Response> punishmentFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrievePunishmentPacket(args[1]));
        punishmentFuture.thenAccept(response -> {
            if (response.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(NamedTextColor.RED, "That punishment does not exist"));
                return;
            }
            Punishment punishment = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishment"), Punishment.class);
            TextComponent inspectMessage = ChatUtil.combine(NamedTextColor.YELLOW, "ID: ", NamedTextColor.WHITE, punishment.getId(), "\n");
            inspectMessage = inspectMessage.append(ChatUtil.combine(NamedTextColor.YELLOW, "Player: ", NamedTextColor.WHITE, response.getData().get("playerName").getAsString(), "\n"));
            inspectMessage = inspectMessage.append(ChatUtil.combine(NamedTextColor.YELLOW, "Type: ", NamedTextColor.WHITE, punishment.getType().name(), "\n\n"));

            inspectMessage = inspectMessage.append(ChatUtil.combine(NamedTextColor.YELLOW, "Reason: ", NamedTextColor.WHITE, punishment.getReason(), "\n\n"));

            inspectMessage = inspectMessage.append(ChatUtil.combine(NamedTextColor.BLUE, "Staff: ", NamedTextColor.WHITE, response.getData().get("staffName").getAsString(), "\n"));
            inspectMessage = inspectMessage.append(ChatUtil.combine(NamedTextColor.BLUE, "Start: ", NamedTextColor.WHITE, ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getStart())), "\n"));
            inspectMessage = inspectMessage.append(ChatUtil.combine(NamedTextColor.BLUE, "End: ", NamedTextColor.WHITE, punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getEnd())), "\n\n"));

            inspectMessage = inspectMessage.append(ChatUtil.combine(NamedTextColor.RED, "History:\n"));
            for (PunishmentHistory history : punishment.getHistory()) {
                inspectMessage = inspectMessage.append(ChatUtil.combine(NamedTextColor.GRAY, "[", ProxyUtil.toReadableTime(history.getDate()), "] ", NamedTextColor.DARK_GRAY, "> ", NamedTextColor.WHITE, history.getType().name(), "\n"));
            }
            ChatUtil.sendMessageBox(sender, NamedTextColor.YELLOW + "" + TextDecoration.BOLD + "Punishment Report", inspectMessage);
        });
    }

    private CompletableFuture<Boolean> edit(UUID staff, String id, PunishmentEditAction action, JsonObject data) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        CompletableFuture<Response> editFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CEditPunishmentPacket(staff, id, action, data));
        editFuture.thenAccept(response -> {
            if (response.getCode() == Response.ResponseCode.SUCCESS) {
                future.complete(true);
            } else {
                future.complete(false);
            }
        });
        return future;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        if (invocation.source().hasPermission("terrabungee.admin")) {
            return true;
        } else {
            invocation.source().sendMessage(
                    ChatUtil.NO_PERMISSION
            );
            return false;
        }
    }
}
