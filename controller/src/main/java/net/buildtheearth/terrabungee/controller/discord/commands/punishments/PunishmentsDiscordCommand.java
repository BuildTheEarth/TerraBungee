package net.buildtheearth.terrabungee.controller.discord.commands.punishments;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.common.players.PunishmentHistory;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordButtonCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.buildtheearth.terrabungee.controller.util.TimeUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Noah Husby
 */
public class PunishmentsDiscordCommand implements IDiscordCommand, IDiscordButtonCommand {
    @Override
    public String getName() {
        return "punishment";
    }

    @Override
    public String getDescription() {
        return "Inspect/edit punishments on the network";
    }

    @Override
    public void execute(User user, UserPermission permission, OffsetDateTime executionTime, SlashCommandEvent event) {
        String subcommand = event.getSubcommandName().toLowerCase(Locale.ROOT);
        if (subcommand.equals("get")) {
            TBPlayer player = getPlayerByIdentifier(event.getOption("player").getAsString());
            if (player == null) {
                event.reply("**" + event.getOption("player").getAsString() + "** has never joined the network!").submit();
                return;
            }
            List<Punishment> punishments = PlayerManager.getInstance().getPunishmentsByPlayer(player.getUniqueID());
            if (punishments == null) {
                event.replyEmbeds(DiscordManager.getInstance().buildEmbed(builder -> {
                    builder.setColor(Color.red);
                    builder.setThumbnail("https://crafatar.com/avatars/" + player.getUniqueID().toString() + "?size=100");
                    builder.setTitle(":clipboard: **" + player.getName() + "** has no punishments on record!");
                })).submit();
                return;
            }
            event.replyEmbeds(DiscordManager.getInstance().buildEmbed(builder -> {
                builder.setColor(Color.red);
                builder.setThumbnail("https://crafatar.com/avatars/" + player.getUniqueID().toString() + "?size=100");
                builder.setTitle("Punishments record for **" + player.getName() + "**");
                for (Punishment punishment : punishments) {
                    TBPlayer staff = TerraBungee.getInstance().getPlayer(punishment.getStaff());
                    builder.addField(punishment.getType().name() + " (#" + punishment.getId() + ")", String.format("By **%s** at **%s**\n**Reason:** %s", staff == null ? "Unknown" : staff.getName(),
                            TimeUtil.toReadableTime(LocalDateTime.parse(punishment.getStart())), punishment.getReason()), false);
                }
            })).submit();
        } else if (subcommand.equals("inspect")) {
            int id = Long.valueOf(Objects.requireNonNull(event.getOption("id")).getAsLong()).intValue();
            InspectionPromptData inspectionPromptData = createGeneralInspection(id);
            ReplyAction action = event.replyEmbeds(inspectionPromptData.getEmbed());
            if (inspectionPromptData.isGenerateButtons()) {
                JsonObject historyData = new JsonObject();
                historyData.addProperty("type", "history");
                historyData.addProperty("id", String.valueOf(id));
                action.addActionRow(DiscordManager.getInstance().generateButtonInteraction(this, ButtonStyle.SECONDARY, historyData, "History"));
            }
            action.submit();
        } else if (subcommand.equals("edit") || (event.getSubcommandGroup() != null) && event.getSubcommandGroup().equalsIgnoreCase("edit")) {
            event.replyEmbeds(DiscordManager.getInstance().buildEmbed(builder -> {
                builder.setColor(Color.red);
                builder.setTitle("Punishment editing is currently disabled on discord! Please edit punishments in-game.");
            })).submit();
        }
    }

    private InspectionPromptData createGeneralInspection(int id) {
        Punishment punishment = PlayerManager.getInstance().getPunishments().get(id);
        if (punishment == null) {
            MessageEmbed embed = DiscordManager.getInstance().buildEmbed(builder -> {
                builder.setColor(Color.red);
                builder.setTitle("That punishment doesn't exist!");
            });
            return new InspectionPromptData(embed, false);
        }

        TBPlayer player = PlayerManager.getInstance().getPlayers().get(punishment.getPlayer());
        TBPlayer staff = PlayerManager.getInstance().getPlayers().get(punishment.getStaff());

        if (player == null) {
            MessageEmbed embed = DiscordManager.getInstance().buildEmbed(builder -> {
                builder.setColor(Color.red);
                builder.setTitle("Failed to get player data from punishment report. This indicates that the database isn't reporting correctly or the data has been tampered with.");
            });
            return new InspectionPromptData(embed, false);
        }

        MessageEmbed embed = DiscordManager.getInstance().buildEmbed(builder -> {
            builder.setColor(Color.red);
            builder.setTitle("Punishment Report");
            builder.setThumbnail("https://crafatar.com/avatars/" + punishment.getPlayer().toString() + "?size=100");
            builder.addField("ID", String.valueOf(punishment.getId()), true);
            builder.addField("Player", player.getName(), true);
            builder.addField("Type", punishment.getType().name(), false);
            builder.addField("Reason", punishment.getReason(), false);
            builder.addField("Staff", staff == null ? "Unknown/Console" : staff.getName(), false);
            builder.addField("Start", TimeUtil.toReadableTime(LocalDateTime.parse(punishment.getStart())), true);
            builder.addField("End", punishment.getEnd() == null ? "None" : TimeUtil.toReadableTime(LocalDateTime.parse(punishment.getEnd())), true);
        });
        return new InspectionPromptData(embed, true);
    }

    private InspectionPromptData createHistoryInspection(int id) {
        Punishment punishment = PlayerManager.getInstance().getPunishments().get(id);
        if (punishment == null) {
            MessageEmbed embed = DiscordManager.getInstance().buildEmbed(builder -> {
                builder.setColor(Color.red);
                builder.setTitle("That punishment doesn't exist!");
            });
            return new InspectionPromptData(embed, false);
        }

        TBPlayer player = PlayerManager.getInstance().getPlayers().get(punishment.getPlayer());

        if (player == null) {
            MessageEmbed embed = DiscordManager.getInstance().buildEmbed(builder -> {
                builder.setColor(Color.red);
                builder.setTitle("Failed to get player data from punishment report. This indicates that the database isn't reporting correctly or the data has been tampered with.");
            });
            return new InspectionPromptData(embed, false);
        }

        MessageEmbed embed = DiscordManager.getInstance().buildEmbed(builder -> {
            builder.setColor(Color.red);
            builder.setTitle("History Report");
            builder.setThumbnail("https://crafatar.com/avatars/" + punishment.getPlayer().toString() + "?size=100");
            builder.addField("ID", String.valueOf(punishment.getId()), true);
            builder.addField("Player", player.getName(), true);
            for (PunishmentHistory history : punishment.getHistory()) {
                builder.addField(TimeUtil.toReadableTime(history.getDate()), history.getType().name(), false);
            }
        });
        return new InspectionPromptData(embed, true);
    }

    @Override
    public void configureData(CommandData data) {
        data.addSubcommands(new SubcommandData("get", "Get punishments for a player")
                .addOption(OptionType.STRING, "player", "Name or UUID of player", true));
        data.addSubcommands(new SubcommandData("inspect", "Inspect a specific punishment")
                .addOption(OptionType.INTEGER, "id", "ID of punishment", true));
        data.addSubcommandGroups(new SubcommandGroupData("edit", "Edit a specific punishment")
                .addSubcommands(new SubcommandData("reason", "Edit the reason")
                        .addOption(OptionType.INTEGER, "id", "ID of punishment", true)
                        .addOption(OptionType.STRING, "reason", "Reason of punishment", true))
                .addSubcommands(new SubcommandData("end", "Edit the end date")
                        .addOption(OptionType.INTEGER, "id", "ID of punishment", true)
                        .addOption(OptionType.INTEGER, "days", "Duration of punishment in days", true))
                .addSubcommands(new SubcommandData("deactivate", "Deactivate the punishment")
                        .addOption(OptionType.INTEGER, "id", "ID of punishment", true))
        );
    }

    private TBPlayer getPlayerByIdentifier(String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            TBPlayer player = PlayerManager.getInstance().getPlayers().get(uuid);
            if (player == null) {
                throw new IllegalArgumentException();
            }
            return player;
        } catch (IllegalArgumentException e) {
            for (TBPlayer player : PlayerManager.getInstance().getPlayers().values()) {
                if (player.getName().equalsIgnoreCase(identifier)) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public void onButtonEvent(JsonObject data, ButtonClickEvent event) {
        String type = data.get("type").getAsString();
        int id = Integer.parseInt(data.get("id").getAsString());
        if (type.equalsIgnoreCase("history")) {
            InspectionPromptData inspectionPromptData = createHistoryInspection(id);
            event.editMessageEmbeds(inspectionPromptData.getEmbed()).submit();
            if (inspectionPromptData.isGenerateButtons()) {
                JsonObject buttonData = new JsonObject();
                buttonData.addProperty("id", String.valueOf(id));
                buttonData.addProperty("type", "general");
                event.editButton(DiscordManager.getInstance().generateButtonInteraction(this, ButtonStyle.PRIMARY, buttonData, "General")).submit();
            }
        } else if (type.equalsIgnoreCase("general")) {
            InspectionPromptData inspectionPromptData = createGeneralInspection(id);
            event.editMessageEmbeds(inspectionPromptData.getEmbed()).submit();
            if (inspectionPromptData.isGenerateButtons()) {
                JsonObject buttonData = new JsonObject();
                buttonData.addProperty("id", String.valueOf(id));
                buttonData.addProperty("type", "history");
                event.editButton(DiscordManager.getInstance().generateButtonInteraction(this, ButtonStyle.SECONDARY, buttonData, "History")).submit();
            }
        }
    }

    @AllArgsConstructor
    @Getter
    private static class InspectionPromptData {
        private final MessageEmbed embed;
        private final boolean generateButtons;
    }
}
