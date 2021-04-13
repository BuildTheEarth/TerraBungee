package net.buildtheearth.terrabungee.controller.discord.embeds;

import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class StaticInstanceAddedEmbed implements IMessageEmbed {

    private TerraBungeeService service;
    private String instance;

    public StaticInstanceAddedEmbed(TerraBungeeService service, String instance) {
        this.service = service;
        this.instance = instance;
    }

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
