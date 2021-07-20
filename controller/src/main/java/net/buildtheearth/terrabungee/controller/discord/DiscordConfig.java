package net.buildtheearth.terrabungee.controller.discord;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.noahhusby.lib.data.storage.Key;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah Husby
 */
@Key("GuildID")
@RequiredArgsConstructor
public class DiscordConfig {
    @Expose
    @SerializedName("GuildID")
    @Getter
    private final long guildId;
    @Expose
    @SerializedName("NotificationChannel")
    @Getter
    @Setter
    private long notificationChannel;
    @Expose
    @SerializedName("AdminRoles")
    @Getter
    private final List<Long> adminRoles = new ArrayList<>();
    @Expose
    @SerializedName("ModeratorRoles")
    @Getter
    private final List<Long> moderatorRoles = new ArrayList<>();
}
