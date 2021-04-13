package net.buildtheearth.terrabungee.controller.discord.embeds;

import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.common.services.Proxy;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ServiceOfflineEmbed implements IMessageEmbed {

    private TerraBungeeService service;

    public ServiceOfflineEmbed(TerraBungeeService service) {
        this.service = service;
    }

    @Override
    public void build(EmbedBuilder e) {
        e.setColor(Color.RED);
        e.setTitle("Service Lost Connection");

        if (service instanceof Proxy) {
            e.setTitle("Proxy Lost Connection");
        } else if (service instanceof Instance) {
            e.setTitle("Instance Lost Connection");
        }

        e.setDescription("**ID: ** " + service.getId());
    }
}
