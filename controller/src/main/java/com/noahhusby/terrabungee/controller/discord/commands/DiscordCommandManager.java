package com.noahhusby.terrabungee.controller.discord.commands;

import com.noahhusby.terrabungee.controller.discord.commands.util.PingDiscordCommand;
import com.noahhusby.terrabungee.controller.discord.commands.util.StatusDiscordCommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah Husby
 */
public class DiscordCommandManager {
    private static DiscordCommandManager instance = null;
    public static DiscordCommandManager getInstance() {
        return instance == null ? instance = new DiscordCommandManager() : instance;
    }

    private DiscordCommandManager() {
        register(new PingDiscordCommand());
        register(new StatusDiscordCommand());
    }

    private final List<IDiscordCommand> commands = new ArrayList<>();

    public void register(IDiscordCommand command) {
        commands.add(command);
    }

    public void execute(String command, User user, TextChannel channel, OffsetDateTime executionTime, String[] args) {
        for(IDiscordCommand c : commands) {
            if(c.getName().equalsIgnoreCase(command)) {
                c.execute(user, channel, executionTime, args);
                return;
            }
        }
    }
}
