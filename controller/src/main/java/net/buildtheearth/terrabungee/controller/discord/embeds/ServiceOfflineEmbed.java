package net.buildtheearth.terrabungee.controller.discord.embeds;

import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.services.Service;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@RequiredArgsConstructor
public class ServiceOfflineEmbed implements IMessageEmbed {

    private final Service service;

    @Override
    public void build(EmbedBuilder e) {
        e.setColor(Color.RED);
        e.setTitle("Service Lost Connection");
        e.setDescription("**ID: ** " + service.getId());
    }
}
