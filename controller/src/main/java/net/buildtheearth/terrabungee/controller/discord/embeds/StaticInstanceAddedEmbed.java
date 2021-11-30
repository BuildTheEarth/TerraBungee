package net.buildtheearth.terrabungee.controller.discord.embeds;

import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.services.Service;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@RequiredArgsConstructor
public class StaticInstanceAddedEmbed implements IMessageEmbed {

    private final Service service;
    private final String instance;

    @Override
    public void build(EmbedBuilder e) {
        e.setTitle("Static Instance Added");
        e.setColor(Color.YELLOW);
        if (service == null) {
            e.setDescription("Static instance __" + instance + "__ was created by the console");
            return;
        }

        e.setDescription("Static instance __" + instance + "__ was created by **" + service.getId() + "**");
    }
}
