/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - TBPlayer.java
 */

package net.buildtheearth.terrabungee.common.players;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TBPlayer {
    @Expose
    @SerializedName("Name")
    protected String name;
    @Expose
    @SerializedName("Attributes")
    protected Map<String, Object> attributes = new HashMap<>();
    @Expose
    @SerializedName("DiscordID")
    protected String discordId;
    @Expose
    @SerializedName("LastSeen")
    protected long lastSeen;
    protected String server;
    protected boolean online;
    protected String proxy;
    @Expose
    @SerializedName("UUID")
    private UUID uuid;

    public TBPlayer() {
    }

    public TBPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Get unique id of player
     *
     * @return Unique id of player
     */
    public UUID getUniqueID() {
        return uuid;
    }

    /**
     * Get the in-game name of player
     *
     * @return In-game name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the map of attributes for the player
     *
     * @return Map of attributes
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Get the current server that the player is on
     *
     * @return Name of server if online, null if not
     */
    public String getServer() {
        return server;
    }

    /**
     * Returns whether the player is online or not
     *
     * @return True if online, false if not
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Returns id of the proxy the player is connected to
     *
     * @return The id of the proxy if online, null if not
     */
    public String getProxy() {
        return proxy;
    }

    /**
     * Returns the discord ID linked to the player
     *
     * @return Id of player, or null if unlinked
     */
    public String getDiscordId() {
        return discordId;
    }

    /**
     * Gets when the player was last seen on the network
     *
     * @return Epoch time of when the player was last on the network
     */
    public long lastSeen() {
        return lastSeen;
    }

    /**
     * Check whether the player contains an attribute with a matching value
     *
     * @param key   Key of attribute
     * @param value Value to match
     * @return True if value matches, false if it doesn't match or if the key doesn't exist
     */
    public boolean match(String key, Object value) {
        if (!attributes.containsKey(key)) {
            return false;
        }
        return attributes.get(key).equals(value);
    }
}
