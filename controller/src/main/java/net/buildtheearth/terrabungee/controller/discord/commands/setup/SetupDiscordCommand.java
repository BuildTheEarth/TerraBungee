package net.buildtheearth.terrabungee.controller.discord.commands.setup;

import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

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
    public void execute(User user, UserPermission permission, OffsetDateTime executionTime, SlashCommandInteractionEvent event) {
        if (permission == UserPermission.ADMIN) {

        }
    }

    @Override
    public void configureData(SlashCommandData data) {

    }
}
