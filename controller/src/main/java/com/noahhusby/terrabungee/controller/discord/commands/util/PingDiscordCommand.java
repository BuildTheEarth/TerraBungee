package com.noahhusby.terrabungee.controller.discord.commands.util;

import com.noahhusby.terrabungee.controller.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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
    public void execute(User user, TextChannel channel, OffsetDateTime executionTime, String[] args) {
        long pong = ChronoUnit.MILLIS.between(executionTime, OffsetDateTime.now());
        channel.sendMessage(String.format(":ping_pong: Pong! **%oms**.", pong)).submit();
    }
}
