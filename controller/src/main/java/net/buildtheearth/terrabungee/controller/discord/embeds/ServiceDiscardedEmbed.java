package net.buildtheearth.terrabungee.controller.discord.embeds;

import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ServiceDiscardedEmbed implements IMessageEmbed {

    private TerraBungeeService service;

    public ServiceDiscardedEmbed(TerraBungeeService service) {
        this.service = service;
    }

    @Override
    public void build(EmbedBuilder e) {
        e.setColor(Color.PINK);
        e.setTitle("Service Discarded");

        e.setDescription("**ID: ** " + service.getId());
    }
}
