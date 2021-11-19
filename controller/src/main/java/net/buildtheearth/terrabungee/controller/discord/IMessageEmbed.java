package net.buildtheearth.terrabungee.controller.discord;

import net.dv8tion.jda.api.EmbedBuilder;

public interface IMessageEmbed {
    void build(EmbedBuilder builder);
}
