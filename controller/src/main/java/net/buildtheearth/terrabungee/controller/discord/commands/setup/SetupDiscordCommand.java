package net.buildtheearth.terrabungee.controller.discord.commands.setup;

import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.controller.discord.DiscordConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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
    public void execute(User user, UserPermission permission, TextChannel channel, OffsetDateTime executionTime, String[] args) {
        if (permission == UserPermission.ADMIN) {
            for (DiscordConfig config : DiscordManager.getInstance().getDiscordConfigs()) {
                if (config.getGuildId() == channel.getGuild().getIdLong()) {
                    channel.sendMessage(DiscordManager.getInstance().buildEmbed(builder -> {
                        builder.setColor(Color.RED);
                        builder.setTitle("This guild is already configured!");
                    })).submit();
                    return;
                }
            }

            DiscordManager.getInstance().getDiscordConfigs().add(new DiscordConfig(channel.getGuild().getIdLong()));
            channel.sendMessage(DiscordManager.getInstance().buildEmbed(builder -> {
                builder.setColor(Color.GREEN);
                builder.setTitle("Added guild to TerraBungee!");
            })).submit();
        }
    }
}
