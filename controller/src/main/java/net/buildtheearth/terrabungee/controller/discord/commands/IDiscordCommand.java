package net.buildtheearth.terrabungee.controller.discord.commands;

import net.buildtheearth.api.discord.UserPermission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.OffsetDateTime;

/**
 * @author Noah Husby
 */
public interface IDiscordCommand {
    String getName();

    String getDescription();

    void execute(User user, UserPermission permission, OffsetDateTime executionTime, SlashCommandInteractionEvent event);

    void configureData(SlashCommandData data);
}
