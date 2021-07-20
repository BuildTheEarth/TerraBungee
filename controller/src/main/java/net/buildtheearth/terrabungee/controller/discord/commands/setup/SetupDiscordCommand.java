package net.buildtheearth.terrabungee.controller.discord.commands.setup;

import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.controller.discord.DiscordConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.time.OffsetDateTime;

/**
 * @author Noah Husby
 */
public class SetupDiscordCommand implements IDiscordCommand {
    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return "f";
    }

    @Override
    public void execute(User user, UserPermission permission, OffsetDateTime executionTime, SlashCommandEvent event) {
        if (permission == UserPermission.ADMIN) {

        }
    }

    @Override
    public void configureData(CommandData data) {

    }
}
