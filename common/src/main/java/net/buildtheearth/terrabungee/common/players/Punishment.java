package net.buildtheearth.terrabungee.common.players;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.noahhusby.lib.data.storage.Key;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Noah Husby
 */
@AllArgsConstructor
@Key("Id")
@Data
public class Punishment {
    @Expose
    @SerializedName("Id")
    private int id;
    @Expose
    @SerializedName("Type")
    private Type type;
    @Expose
    @SerializedName("Staff")
    private UUID staff;
    @Expose
    @SerializedName("Player")
    private UUID player;
    @Expose
    @SerializedName("Start")
    private String start;
    @Expose
    @SerializedName("End")
    private String end;
    @Expose
    @SerializedName("Reason")
    private String reason;
    @Expose
    @SerializedName("History")
    private List<PunishmentHistory> history;

    public boolean isActive() {
        try {
            return end == null || LocalDateTime.now().isBefore(LocalDateTime.parse(end));
        } catch (Exception ignored) {
            return false;
        }
    }

    public enum Type {
        BAN, KICK, MUTE
    }
}
