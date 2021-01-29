package com.noahhusby.terrabungee.controller.discord;

import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.config.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiscordManager {
    private static DiscordManager instance = null;

    public static DiscordManager getInstance() {
        if(instance == null) instance = new DiscordManager();
        return instance;
    }

    private JDA bot;
    private TextChannel channel;
    private static final ExecutorService botThread = TerraBungeeUtil.newSingleThreadExecutor("terrabungee-bot");

    private DiscordManager() {
        if(ConfigHandler.botToken.equalsIgnoreCase("")) return;
        loadBot();
    }

    public void loadBot() {
        botThread.submit(() -> {
            try {
                bot = JDABuilder.createDefault(ConfigHandler.botToken)
                        .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                        .enableIntents(GatewayIntent.GUILD_MESSAGES)
                        .addEventListeners(new DiscordListener()).build();
            } catch (LoginException e) {
                TerraBungeeController.logger.warning("Failed to initialize discord bot! Please check the token and try again.");
            }
            TerraBungeeController.getInstance().getGeneralThreads().schedule(() -> {
                Guild g = bot.getGuildById(ConfigHandler.guildID);
                if(g == null) return;
                boolean adminRole = false;
                for(Role r : g.getRoles()) {
                    if(r.getName().equalsIgnoreCase("TBAdmin")) adminRole = true;
                }
                if(!adminRole) {
                    g.createRole().setMentionable(true).setName("TBAdmin").submit();
                }
            }, 5, TimeUnit.SECONDS);
        });
    }

    public JDA getBot() {
        return bot;
    }

    public void send(IMessageEmbed emb) {
        if(ConfigHandler.botToken.equalsIgnoreCase("")) return;
        botThread.submit(() -> {
            if(channel == null) {
                Guild g = bot.getGuildById(ConfigHandler.guildID);
                channel = g.getTextChannelById(ConfigHandler.channelID);
            }

            EmbedBuilder e = new EmbedBuilder();
            e.setTimestamp(new Date().toInstant());
            e.setFooter("TerraBungee by Noah Husby");
            channel.sendMessage(emb.build(e).build()).queue();
        });
    }
}

