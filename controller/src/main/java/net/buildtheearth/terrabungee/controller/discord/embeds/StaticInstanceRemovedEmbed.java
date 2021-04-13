package net.buildtheearth.terrabungee.controller.discord.embeds;

import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class StaticInstanceRemovedEmbed implements IMessageEmbed {

    private TerraBungeeService service;
    private String instance;

    public StaticInstanceRemovedEmbed(TerraBungeeService service, String instance) {
        this.service = service;
        this.instance = instance;
    }

    @Override
    public void build(EmbedBuilder e) {
        e.setTitle("Static Instance Removed");
        e.setColor(Color.YELLOW);
        if (service == null) {
            e.setDescription("Static instance __" + instance + "__ was removed by the console");
            return;
        }

        e.setDescription("Static instance __" + instance + "__ was removed by **" + service.getId() + "**");
    }
}
