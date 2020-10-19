package com.noahhusby.terrabungee.controller.discord.embeds;

import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ProxyAddedEmbed implements IMessageEmbed {

    private ITerraBungeeService service;

    public ProxyAddedEmbed(ITerraBungeeService service) {
        this.service = service;
    }

    @Override
    public EmbedBuilder build(EmbedBuilder e) {
        e.setColor(Color.decode("#a7a888"));
        e.setTitle("New Proxy Added");
        e.setDescription("**ID: ** "+service.getId());
        return e;
    }
}
