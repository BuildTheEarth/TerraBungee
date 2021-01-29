package com.noahhusby.terrabungee.controller.discord.embeds;

import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import com.noahhusby.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ControllerStartedEmbed implements IMessageEmbed {
    @Override
    public void build(EmbedBuilder e) {
        e.setColor(Color.cyan);
        e.setDescription("**Controller Started!**");
    }
}
