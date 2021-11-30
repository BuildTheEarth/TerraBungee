package net.buildtheearth.terrabungee.controller.discord.embeds;

import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.common.services.Proxy;
import net.buildtheearth.terrabungee.common.services.Service;
import net.buildtheearth.terrabungee.controller.discord.IMessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@RequiredArgsConstructor
public class ServiceReconnectedEmbed implements IMessageEmbed {

    private final Service service;

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
