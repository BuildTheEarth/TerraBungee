package net.buildtheearth.terrabungee.controller.discord;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.noahhusby.lib.data.storage.Key;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah Husby
 */
@Key("GuildID")
@RequiredArgsConstructor
@Getter
public class GuildConfig {
    @Expose
    @SerializedName("GuildID")
    private final long guildId;
    @Expose
    @SerializedName("BotID")
    @Setter
    private int botId;
    @Expose
    @SerializedName("NotificationChannel")
    @Setter
    private long notificationChannel;
    @Expose
    @SerializedName("StaffRoles")
    private final List<Long> staffRoles = new ArrayList<>();

    public JDA getBot() {
        BotConfig config = DiscordManager.getInstance().getBotConfigs().get(botId);
        if(config == null) {
            return null;
        }
        return config.getBot();
    }

    public Guild getGuild() {
        JDA bot = getBot();
        if(bot == null) {
            return null;
        }
        return bot.getGuildById(guildId);
    }

    public TextChannel getNotificationTextChannel() {
        Guild guild = getGuild();
        if(guild == null) {
            return null;
        }
        return guild.getTextChannelById(notificationChannel);
    }

    public boolean isConfigured() {
        return botId != 0 && notificationChannel != 0;
    }
}
