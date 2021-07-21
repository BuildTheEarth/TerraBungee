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
        return DiscordManager.getInstance().getBotConfigs().get(botId).getBot();
    }

    public Guild getGuild() {
        return getBot().getGuildById(guildId);
    }

    public TextChannel getNotificationChannel() {
        return getGuild().getTextChannelById(notificationChannel);
    }
}
