package net.buildtheearth.terrabungee.controller.discord;

import com.noahhusby.lib.data.storage.StorageList;
import lombok.Getter;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DiscordManager {
    private static DiscordManager instance = null;

    public static DiscordManager getInstance() {
        if (instance == null) {
            instance = new DiscordManager();
        }
        return instance;
    }

    private JDA bot;
    private TextChannel channel;
    private final ExecutorService botThread = TerraBungeeUtil.newSingleThreadExecutor("terrabungee-bot");
    @Getter
    private final StorageList<DiscordConfig> discordConfigs = new StorageList<>(DiscordConfig.class);

    private DiscordManager() {
        if (ConfigHandler.botToken.equalsIgnoreCase("")) {
            return;
        }
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
                if (g == null) {
                    return;
                }
                boolean adminRole = false;
                for (Role r : g.getRoles()) {
                    if (r.getName().equalsIgnoreCase("TBAdmin")) {
                        adminRole = true;
                    }
                }
                if (!adminRole) {
                    g.createRole().setMentionable(true).setName("TBAdmin").submit();
                }
            }, 2, TimeUnit.SECONDS);
        });
    }

    public JDA getBot() {
        return bot;
    }

    public MessageEmbed buildEmbed(Consumer<EmbedBuilder> builder) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTimestamp(new Date().toInstant());
        e.setFooter("TerraBungee by Noah Husby");
        builder.accept(e);
        return e.build();
    }

    public void send(IMessageEmbed emb) {
        if (ConfigHandler.botToken.equalsIgnoreCase("")) {
            return;
        }
        botThread.submit(() -> {
            if (channel == null) {
                Guild g = bot.getGuildById(ConfigHandler.guildID);
                if (g == null) {
                    return;
                }
                channel = g.getTextChannelById(ConfigHandler.channelID);
                if (channel == null) {
                    return;
                }
            }

            channel.sendMessage(buildEmbed(emb::build)).submit();
        });
    }

    public DiscordConfig getConfigByGuild(Guild guild) {
        for (DiscordConfig config : getDiscordConfigs()) {
            if (guild.getIdLong() == config.getGuildId()) {
                return config;
            }
        }
        return null;
    }
}

