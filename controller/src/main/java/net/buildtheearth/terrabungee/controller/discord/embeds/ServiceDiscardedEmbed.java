package net.buildtheearth.terrabungee.controller.discord.embeds;

import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@RequiredArgsConstructor
public class ServiceDiscardedEmbed implements IMessageEmbed {

    private final TerraBungeeService service;

    @Override
    public void build(EmbedBuilder e) {
        e.setColor(Color.PINK);
        e.setTitle("Service Discarded");

        e.setDescription("**ID: ** " + service.getId());
    }
}
