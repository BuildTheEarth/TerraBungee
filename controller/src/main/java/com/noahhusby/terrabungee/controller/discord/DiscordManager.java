package com.noahhusby.terrabungee.controller.discord;

import com.noahhusby.terrabungee.controller.config.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordManager {
    private static DiscordManager instance = null;

    public static DiscordManager getInstance() {
        if(instance == null) instance = new DiscordManager();
        return instance;
    }

    private JDA bot;
    private TextChannel channel;
    private static ExecutorService botThreadExecutor;

    private DiscordManager() {
        botThreadExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r, "Bot"));
        if(ConfigHandler.botToken.equalsIgnoreCase("")) return;
        execute(() -> {
            try {
                bot = JDABuilder.createDefault(ConfigHandler.botToken)
                        .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                        .enableIntents(GatewayIntent.GUILD_MESSAGES)
                        .addEventListeners(new DiscordListener()).build();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        });
    }

    public static void execute(Runnable r) {
        botThreadExecutor.submit(r);
    }

    public JDA getBot() {
        return bot;
    }

    public void send(IMessageEmbed emb) {
        if(ConfigHandler.botToken.equalsIgnoreCase("")) return;
        execute(() -> {
            if(channel == null) {
                Guild g = bot.getGuildById(ConfigHandler.guildID);
                channel = g.getTextChannelById(ConfigHandler.channelID);
            }

            EmbedBuilder e = new EmbedBuilder();
            e.setFooter("TerraBungee by Noah Husby \u2022 Today at " + new SimpleDateFormat("hh:mm a").format(new Date()));
            channel.sendMessage(emb.build(e).build()).queue();
        });
    }
}

