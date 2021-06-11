package net.buildtheearth.terrabungee.common.players;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

/**
 * @author Noah Husby
 */
@AllArgsConstructor
@Data
public class PunishmentHistory {
    @Expose
    private UUID player;
    @Expose
    private Type type;
    @Expose
    private Date date;
    @Expose
    private JsonObject data;

    public enum Type {
        CREATION, EDIT_TIME, EDIT_REASON
    }
}
