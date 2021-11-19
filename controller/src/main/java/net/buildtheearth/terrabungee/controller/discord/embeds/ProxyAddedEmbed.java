package net.buildtheearth.terrabungee.controller.discord.embeds;

import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@RequiredArgsConstructor
public class ProxyAddedEmbed implements IMessageEmbed {

    private final TerraBungeeService service;

    @Override
    public void build(EmbedBuilder e) {
        e.setColor(Color.decode("#a7a888"));
        e.setTitle("New Proxy Added");
        e.setDescription("**ID: ** " + service.getId());
    }
}
