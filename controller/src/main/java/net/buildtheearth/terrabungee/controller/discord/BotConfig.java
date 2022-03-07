package net.buildtheearth.terrabungee.controller.discord;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.noahhusby.lib.data.storage.Key;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

/**
 * @author Noah Husby
 */
@Key("Id")
@RequiredArgsConstructor
@Getter
public class BotConfig {
    @Expose
    @SerializedName("Id")
    private final int id;
    @Expose
    @SerializedName("Name")
    @Setter
    private String name;
    @Expose
    @SerializedName("Token")
    @Setter
    private String token;

    private JDA bot;

    public void initBot() {
        if (isConfigured()) {
            try {
                bot = JDABuilder.createDefault(token)
                        .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                        .enableIntents(GatewayIntent.GUILD_MESSAGES)
                        .addEventListeners(new DiscordListener()).build();
                bot.setAutoReconnect(true);
            } catch (LoginException e) {
                DiscordManager.getInstance().getLogger().warn(String.format("Failed to initialize %s! Please check the token and try again.", name));
            }
        }
    }

    public void shutdown() {
        if (bot != null) {
            try {
                bot.shutdown();
                bot = null;
            } catch (IllegalStateException exception) {
                DiscordManager.getInstance().getLogger().warn("Failed to shutdown: " + getName());
            }
        }
    }

    public boolean isConfigured() {
        return name != null && token != null;
    }

    public boolean isEnabled() {
        return bot != null;
    }
}
