package net.buildtheearth.terrabungee.common.logging;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerLogContainer {
    @Expose
    @SerializedName("UUID")
    private final UUID uuid;
    @Expose
    @SerializedName("Entries")
    private List<LogEntry> entries = new ArrayList<>();
}
