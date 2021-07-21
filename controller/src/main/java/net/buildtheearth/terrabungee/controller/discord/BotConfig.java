package net.buildtheearth.terrabungee.controller.discord;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.noahhusby.lib.data.storage.Key;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah Husby
 */
@Key("GuildID")
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
        try {
            bot = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new DiscordListener()).build();
            bot.setAutoReconnect(true);
        } catch (LoginException e) {
            TerraBungee.getInstance().getLogger().warning(String.format("Failed to initialize %s! Please check the token and try again.", name));
        }
    }

    public void shutdown() {
        bot.shutdown();
    }
}
