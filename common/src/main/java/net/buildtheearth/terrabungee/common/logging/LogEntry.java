package net.buildtheearth.terrabungee.common.logging;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.noahhusby.lib.data.storage.Key;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Key("Time")
@RequiredArgsConstructor
public class LogEntry {
    @SerializedName("Time")
    @Expose
    private final long time;
    @SerializedName("Type")
    @Expose
    private final LogEntryType type;
    @SerializedName("Data")
    @Expose
    private final JsonObject data;

    public LogEntry(long time, LogEntryType type) {
        this(time, type, new JsonObject());
    }
}
