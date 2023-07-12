package net.buildtheearth.terrabungee.controller.discord.commands.util;

import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Noah Husby
 */
public class PingDiscordCommand implements IDiscordCommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "ff";
    }

    @Override
    public void execute(User user, UserPermission permission, OffsetDateTime executionTime, SlashCommandInteractionEvent event) {
        long pong = ChronoUnit.MILLIS.between(executionTime, OffsetDateTime.now());
        event.reply(String.format(":ping_pong: Pong! **%oms**.", pong)).submit();
    }

    @Override
    public void configureData(SlashCommandData data) {

    }
}
