package net.buildtheearth.terrabungee.controller.discord.embeds;

import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ControllerStoppedEmbed implements IMessageEmbed {
    @Override
    public void build(EmbedBuilder e) {
        e.setColor(Color.red);
        e.setDescription("**Controller Stopped!**");
    }
}
