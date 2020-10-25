package com.noahhusby.terrabungee.controller.discord.embeds;

import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class StaticInstanceAddedEmbed implements IMessageEmbed {

    private ITerraBungeeService service;
    private String instance;

    public StaticInstanceAddedEmbed(ITerraBungeeService service, String instance) {
        this.service = service;
        this.instance = instance;
    }

    @Override
    public EmbedBuilder build(EmbedBuilder e) {
        e.setTitle("Static Instance Added");
        e.setColor(Color.YELLOW);
        if(service == null) {
            e.setDescription("Static instance __" + instance+ "__ was created by the console");
            return e;
        }

        e.setDescription("Static instance __" + instance+ "__ was created by **" + service.getId() + "**");
        return e;
    }
}
