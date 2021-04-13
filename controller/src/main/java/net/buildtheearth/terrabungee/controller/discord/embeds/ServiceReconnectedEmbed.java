package net.buildtheearth.terrabungee.controller.discord.embeds;

import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.common.services.Proxy;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ServiceReconnectedEmbed implements IMessageEmbed {

    private TerraBungeeService service;

    public ServiceReconnectedEmbed(TerraBungeeService service) {
        this.service = service;
    }

    @Override
    public void build(EmbedBuilder e) {
        e.setColor(Color.GREEN);

        if (service instanceof Proxy) {
            e.setTitle("Proxy Reconnected");
        } else if (service instanceof Instance) {
            e.setTitle("Instance Reconnected");
        }

        e.setDescription("**ID: ** " + service.getId());
    }
}
