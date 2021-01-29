package com.noahhusby.terrabungee.controller.discord;

import net.dv8tion.jda.api.EmbedBuilder;

public interface IMessageEmbed {
    public void build(EmbedBuilder builder);
}
