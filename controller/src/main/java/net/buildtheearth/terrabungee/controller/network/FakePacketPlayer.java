package net.buildtheearth.terrabungee.controller.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import net.buildtheearth.terrabungee.common.players.TBPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class FakePacketPlayer {

    @Expose
    @SerializedName("UUID")
    private UUID uuid;
    @Expose
    @SerializedName("Name")
    protected String name;
    @Expose
    @SerializedName("Attributes")
    protected Map<String, Object> attributes;
    @Expose
    @SerializedName("DiscordID")
    protected String discordId;
    @Expose
    @SerializedName("LastSeen")
    protected long lastSeen;

    @Expose
    @SerializedName("Server")
    protected String server;

    @Expose
    @SerializedName("Online")
    protected boolean online;

    @Expose
    @SerializedName("Proxy")
    protected String proxy;

    public FakePacketPlayer(TBPlayer copyFrom) {
        this.name = copyFrom.getName();
        this.uuid = copyFrom.getUniqueID();
        this.lastSeen = copyFrom.lastSeen();
        this.server = copyFrom.getServer();
        this.discordId = copyFrom.getDiscordId();
        this.attributes = copyFrom.getAttributes();
        this.online = copyFrom.isOnline();
        this.proxy = copyFrom.getProxy();
    }
}
