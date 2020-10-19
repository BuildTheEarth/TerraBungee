package com.noahhusby.terrabungee.controller.discord.embeds;

import com.noahhusby.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ControllerStoppedEmbed implements IMessageEmbed {
    @Override
    public EmbedBuilder build(EmbedBuilder e) {
        e.setColor(Color.red);
        e.setDescription("**Controller Stopped!**");
        return e;
    }
}
