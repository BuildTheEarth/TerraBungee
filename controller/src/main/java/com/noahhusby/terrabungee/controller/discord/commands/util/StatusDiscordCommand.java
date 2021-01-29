package com.noahhusby.terrabungee.controller.discord.commands.util;

import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import com.noahhusby.terrabungee.controller.discord.commands.IDiscordCommand;
import com.noahhusby.terrabungee.controller.players.PlayerManager;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.OffsetDateTime;

/**
 * @author Noah Husby
 */
public class StatusDiscordCommand implements IDiscordCommand {
    @Override
    public String getName() {
        return "status";
    }

    @Override
    public void execute(User user, TextChannel channel, OffsetDateTime executionTime, String[] args) {
        channel.sendMessage(DiscordManager.getInstance().buildEmbed(builder -> {
            builder.setColor(Color.getHSBColor(20, 100, 50));
            builder.setTitle("System Status");
            builder.addField("Total Players", String.valueOf(PlayerManager.getInstance().getPlayers().size()), true);
            builder.addField("Online Players", String.valueOf(PlayerManager.getInstance().getOnlinePlayerRegistry().size()), true);
            builder.addField("Active Services", String.valueOf(ServiceManager.getInstance().getServices().size()), false);
            builder.addField("Disconnected Services", String.valueOf(ServiceManager.getInstance().getTotalDisconnectedServices()), false);
        })).submit();
    }
}
