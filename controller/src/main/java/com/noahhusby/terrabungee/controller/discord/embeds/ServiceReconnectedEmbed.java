package com.noahhusby.terrabungee.controller.discord.embeds;

import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.api.services.Proxy;
import com.noahhusby.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ServiceReconnectedEmbed implements IMessageEmbed {

    private ITerraBungeeService service;

    public ServiceReconnectedEmbed(ITerraBungeeService service) {
        this.service = service;
    }

    @Override
    public EmbedBuilder build(EmbedBuilder e) {
        e.setColor(Color.GREEN);

        if(service instanceof Proxy) {
            e.setTitle("Proxy Reconnected");
        } else if(service instanceof Instance) {
            e.setTitle("Instance Reconnected");
        }

        e.setDescription("**ID: ** "+service.getId());
        return e;
    }
}
