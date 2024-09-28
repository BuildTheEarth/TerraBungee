package net.buildtheearth.terrabungee.proxy.commands;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
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
import net.buildtheearth.terrabungee.proxy.util.VelocityChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

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
public class PunishmentCommand implements SimpleCommand {

    @Override
    public void execute(SimpleCommand.Invocation invocation) {

        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 1) {
            sender.sendMessage(
                    Component.text()
                            .append(VelocityChatUtil.PREFIX)
                            .append(Component.text("Usage: /punishment <get | edit | inspect>", NamedTextColor.RED))
            );

            return;
        }
        if (args[0].equalsIgnoreCase("get")) {
            executeGet(invocation);
        } else if (args[0].equalsIgnoreCase("edit")) {
            executeEdit(invocation);
        } else if (args[0].equalsIgnoreCase("inspect")) {
            executeInspect(invocation);
        } else {
            sender.sendMessage(
                    Component.text()
                            .append(VelocityChatUtil.PREFIX)
                            .append(Component.text("Usage: /punishment <get | edit | inspect>", NamedTextColor.RED))
            );

        }
    }

    private void executeGet(SimpleCommand.Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 2) {
            sender.sendMessage(
                    Component.text()
                            .append(VelocityChatUtil.PREFIX)
                            .append(Component.text("Usage: /punishment get <player>", NamedTextColor.RED))
            );
            return;
        }
        String playerName = args[1];
        CompletableFuture<TBPlayer> playerFuture = TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(playerName);
        playerFuture.thenAccept(tbPlayer -> {
            if (tbPlayer == null) {
                sender.sendMessage(
                        Component.text()
                                .append(VelocityChatUtil.PREFIX)
                                .append(Component.text(playerName, NamedTextColor.YELLOW))
                                .append(Component.text(" has never joined the network!", NamedTextColor.GRAY))
                );
                return;
            }
            CompletableFuture<Response> punishmentFuture = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrievePunishmentsPacket(tbPlayer.getUniqueID()));
            punishmentFuture.thenAccept(response -> {
                if (response.getCode() == Response.ResponseCode.ERROR) {
                    sender.sendMessage(
                            Component.text()
                                    .append(VelocityChatUtil.PREFIX)
                                    .append(Component.text(playerName, NamedTextColor.YELLOW))
                                    .append(Component.text(" has no punishments on record!", NamedTextColor.GRAY))
                    );
                    return;
                }
                Type punishmentListType = new TypeToken<ArrayList<Punishment>>() {}.getType();
                List<Punishment> punishments = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishments"), punishmentListType);
                sender.sendMessage(
                        Component.text()
                                .append(VelocityChatUtil.PREFIX)
                                .append(Component.text("Punishments for ", NamedTextColor.GRAY))
                                .append(Component.text(tbPlayer.getName(), NamedTextColor.YELLOW))
                                .append(Component.text(":", NamedTextColor.GRAY))
                );
                for (Punishment punishment : punishments) {
                    TextComponent.Builder punishmentMessage =
                            Component.text()
                                    .append(Component.text("#" + punishment.getId(), NamedTextColor.RED))
                                    .append(Component.text(" - ", NamedTextColor.GRAY))
                                    .append(Component.text(punishment.getType().name(), NamedTextColor.BLUE))
                                    .append(Component.text(" "));
                    TerraBungeeProxy.getInstance().getTerraBungee().getPlayer(punishment.getStaff()).thenAccept(staffPlayer -> {
                        TextComponent.Builder hoverMessage =
                                Component.text()
                                        .append(Component.text("Reason: ", NamedTextColor.GRAY))
                                        .append(Component.text(punishment.getReason(), NamedTextColor.WHITE))
                                        .append(Component.newline())
                                        .append(Component.text("Start: ", NamedTextColor.GRAY))
                                        .append(Component.text(ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getStart())), NamedTextColor.WHITE))
                                        .append(Component.newline())
                                        .append(Component.text("End: ", NamedTextColor.GRAY))
                                        .append(Component.text(punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getEnd())), NamedTextColor.WHITE))
                                        .append(Component.newline())
                                        .append(Component.text("Staff: ", NamedTextColor.GRAY))
                                        .append(Component.text(staffPlayer == null ? "Unknown" : staffPlayer.getName(), NamedTextColor.WHITE));

                        punishmentMessage.hoverEvent(HoverEvent.showText(hoverMessage));
                        TextComponent interaction = new TextComponent("[*]", NamedTextColor.YELLOW);
                        interaction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishment inspect " + punishment.getId()));
                        interaction.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Inspect the punishment").create()));
                        punishmentMessage.addExtra(interaction);
                        if (punishment.isActive()) {
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
        if (args.length < 3) {
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
            if (response.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "That punishment does not exist"));
                return;
            }
            Punishment punishment = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishment"), Punishment.class);
            if (punishment.getType() == Punishment.Type.KICK) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "You cannot edit this type of punishment!"));
                return;
            }
            if (command.equalsIgnoreCase("deactivate")) {
                if (!punishment.isActive()) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "That punishment is already inactive!"));
                    return;
                }
                edit(staff, args[1], PunishmentEditAction.DEACTIVATE, new JsonObject()).thenAccept(a -> {
                    if (a) {
                        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GREEN, "Successfully deactivated the punishment"));
                    } else {
                        sender.sendMessage(ChatUtil.getNoContact());
                    }
                });
            } else if (command.equalsIgnoreCase("end")) {
                if (args.length < 4) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment edit <punishment id> end <days>"));
                    return;
                }
                JsonObject data = new JsonObject();
                String lengthString = args[3];
                long length;
                try {
                    if (lengthString.equals("0")) {
                        length = 0;
                    } else {
                        length = DateUtil.parseDateDiff(lengthString, true, true);
                    }
                } catch (Exception ignored) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.BLUE, lengthString, ChatColor.GRAY, " is not a valid length! Enter the amount of time for the punishment (Ex: 1d12h), or enter ", ChatColor.YELLOW, "0 ", ChatColor.GRAY, "for a permanent punishment."));
                    return;
                }
                data.addProperty("length", length);
                edit(staff, args[1], PunishmentEditAction.END, data).thenAccept(a -> {
                    if (a) {
                        sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.GRAY, "Successfully changed the end date from ", ChatColor.YELLOW, punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getEnd())), ChatColor.GRAY, " to ",
                                ChatColor.YELLOW, ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getStart()).plusSeconds(length == 0 ? 0 : length / 1000))));
                    } else {
                        sender.sendMessage(ChatUtil.getNoContact());
                    }
                });
            } else if (command.equalsIgnoreCase("reason")) {
                if (args.length < 4) {
                    sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "Usage: /punishment edit <punishment id> reason <reason>"));
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
        if (args.length < 2) {
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
            if (response.getCode() == Response.ResponseCode.ERROR) {
                sender.sendMessage(ChatUtil.titleAndCombine(ChatColor.RED, "That punishment does not exist"));
                return;
            }
            Punishment punishment = TerraBungeeUtil.GSON.fromJson(response.getData().get("punishment"), Punishment.class);
            BaseComponent inspectMessage = ChatUtil.combine(ChatColor.YELLOW, "ID: ", ChatColor.WHITE, punishment.getId(), "\n");
            inspectMessage.addExtra(ChatUtil.combine(ChatColor.YELLOW, "Player: ", ChatColor.WHITE, response.getData().get("playerName").getAsString(), "\n"));
            inspectMessage.addExtra(ChatUtil.combine(ChatColor.YELLOW, "Type: ", ChatColor.WHITE, punishment.getType().name(), "\n\n"));

            inspectMessage.addExtra(ChatUtil.combine(ChatColor.YELLOW, "Reason: ", ChatColor.WHITE, punishment.getReason(), "\n\n"));

            inspectMessage.addExtra(ChatUtil.combine(ChatColor.BLUE, "Staff: ", ChatColor.WHITE, response.getData().get("staffName").getAsString(), "\n"));
            inspectMessage.addExtra(ChatUtil.combine(ChatColor.BLUE, "Start: ", ChatColor.WHITE, ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getStart())), "\n"));
            inspectMessage.addExtra(ChatUtil.combine(ChatColor.BLUE, "End: ", ChatColor.WHITE, punishment.getEnd() == null ? "None" : ProxyUtil.toReadableTime(LocalDateTime.parse(punishment.getEnd())), "\n\n"));

            inspectMessage.addExtra(ChatUtil.combine(ChatColor.RED, "History:\n"));
            for (PunishmentHistory history : punishment.getHistory()) {
                inspectMessage.addExtra(ChatUtil.combine(ChatColor.GRAY, "[", ProxyUtil.toReadableTime(history.getDate()), "] ", ChatColor.DARK_GRAY, "> ", ChatColor.WHITE, history.getType().name(), "\n"));
            }
            ChatUtil.sendMessageBox(sender, ChatColor.YELLOW + "" + ChatColor.BOLD + "Punishment Report", inspectMessage);
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
                    VelocityChatUtil.NO_PERMISSION
            );
            return false;
        }
    }
}
