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

    public synchronized void initBot() {
        if(isConfigured()) {
            try {
                if (bot != null && bot.getStatus() != JDA.Status.SHUTDOWN
                        && bot.getStatus() != JDA.Status.SHUTTING_DOWN
                        && bot.getStatus() != JDA.Status.FAILED_TO_LOGIN) {
                    return;
                }
                bot = JDABuilder.createLight(token)
                        .addEventListeners(new DiscordListener(this)).build();
                bot.setAutoReconnect(true);
            } catch (Exception e) {
                bot = null;
                TerraBungee.getInstance().getLogger().warning(String.format(
                        "Failed to initialize %s: %s", name, safeMessage(e)));
            }
        }
    }

    public synchronized void shutdown() {
        if(bot != null) {
            try {
                bot.shutdown();
                bot = null;
            } catch (IllegalStateException exception) {
                TerraBungee.getInstance().getLogger().warning("Failed to shutdown: " + getName());
            }
        }
    }

    public boolean isConfigured() {
        return name != null && token != null;
    }

    public boolean isEnabled() {
        return bot != null && bot.getStatus() == JDA.Status.CONNECTED;
    }

    public boolean isRunning() {
        return bot != null
                && bot.getStatus() != JDA.Status.SHUTDOWN
                && bot.getStatus() != JDA.Status.SHUTTING_DOWN
                && bot.getStatus() != JDA.Status.FAILED_TO_LOGIN;
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        return exception.getClass().getSimpleName() + (message == null ? "" : ": " + message);
    }
}
