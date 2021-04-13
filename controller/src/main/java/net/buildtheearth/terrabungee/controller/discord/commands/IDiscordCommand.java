package net.buildtheearth.terrabungee.controller.discord.commands;

import net.buildtheearth.terrabungee.controller.discord.UserPermission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;

/**
 * @author Noah Husby
 */
public interface IDiscordCommand {
    String getName();

    void execute(User user, UserPermission permission, TextChannel channel, OffsetDateTime executionTime, String[] args);
}
