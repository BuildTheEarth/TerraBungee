/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - PlayerManager.java
 */

package net.buildtheearth.terrabungee.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrieveUncachedPlayerPacket;
import net.buildtheearth.terrabungee.client.network.S2C.S2CUpdateAttributeID;
import net.buildtheearth.terrabungee.client.util.Manager;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.util.EventHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Noah Husby
 */
public class PlayerManager extends Manager {

    private Map<UUID, TBPlayer> onlinePlayers = new HashMap<>();

    protected PlayerManager(TerraBungeeClient tb) {
        super(tb);
    }

    /**
     * Get player by username
     *
     * @param name Username
     * @return {@link TBPlayer}
     */
    public CompletableFuture<TBPlayer> getPlayer(@NonNull String name) {
        CompletableFuture<TBPlayer> future = new CompletableFuture<>();
        for (TBPlayer player : Lists.newArrayList(onlinePlayers.values())) {
            if (player.getName().equalsIgnoreCase(name)) {
                complete(future, player);
                return future;
            }
        }

        tb.getNetworkManager().send(new S2CRetrieveUncachedPlayerPacket(name)).thenAccept(response -> {
            if (response.getData() == null || response.getCode() != Response.ResponseCode.SUCCESS) {
                future.complete(null);
                return;
            }

            future.complete(handleEvent(TerraBungeeUtil.GSON.fromJson(response.getData(), TBPlayer.class)));
        });

        return future;
    }

    /**
     * Get player by uuid
     *
     * @param uuid UUID
     * @return {@link TBPlayer}
     */
    public CompletableFuture<TBPlayer> getPlayer(@NonNull UUID uuid) {
        CompletableFuture<TBPlayer> future = new CompletableFuture<>();
        TBPlayer player = onlinePlayers.get(uuid);
        if (player != null) {
            complete(future, player);
            return future;

        }

        tb.getNetworkManager().send(new S2CRetrieveUncachedPlayerPacket(uuid.toString())).thenAccept(response -> {
            if (response.getData() == null || response.getCode() != Response.ResponseCode.SUCCESS) {
                future.complete(null);
                return;
            }

            future.complete(handleEvent(TerraBungeeUtil.GSON.fromJson(response.getData(), TBPlayer.class)));
        });

        return future;
    }

    /**
     * Gets list of online players from local cache
     *
     * @return List of {@link TBPlayer}
     */
    public CompletableFuture<Map<UUID, TBPlayer>> getOnlinePlayers() {
        CompletableFuture<Map<UUID, TBPlayer>> future = new CompletableFuture<>();
        tb.getGeneralThreads().schedule(() -> future.complete(ImmutableMap.copyOf(onlinePlayers)), 40, TimeUnit.MILLISECONDS);
        // TODO: Allow fetching from controller
        return future;
    }

    /**
     * Sets the online cached player list from the controller's cache hit
     * <p>
     * Note: This should only be called by the network manager
     *
     * @param players
     */
    public void onlineCacheHit(@NonNull List<TBPlayer> players) {
        Map<UUID, TBPlayer> temp = Maps.newHashMap(onlinePlayers);
        List<UUID> playerIDs = Lists.newArrayList();
        for (TBPlayer player : players) {
            playerIDs.add(player.getUniqueID());
            TBPlayer p = onlinePlayers.get(player.getUniqueID());
            if (p == null) {
                temp.put(player.getUniqueID(), handleEvent(player));
            } else {
                temp.put(p.getUniqueID(), handleEvent(player));
            }
        }

        List<UUID> remove = Lists.newArrayList();
        for (UUID u : temp.keySet()) {
            if (!playerIDs.contains(u)) {
                remove.add(u);
            }
        }

        for (UUID u : remove) {
            temp.remove(u);
        }

        onlinePlayers = temp;
    }

    /**
     * Get a cached online player by UUID
     *
     * @param uuid {@link UUID}
     * @return {@link TBPlayer}
     */
    public TBPlayer getCachedOnlinePlayer(@NonNull UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    /**
     * Get a cached online player by name
     *
     * @param name Username of player
     * @return {@link TBPlayer}
     */
    public TBPlayer getCachedOnlinePlayer(@NonNull String name) {
        for (TBPlayer p : ImmutableMap.copyOf(onlinePlayers).values()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Get map of cached online players
     *
     * @return Map of cached online players
     */
    public Map<UUID, TBPlayer> getCachedOnlinePlayers() {
        return onlinePlayers;
    }

    private void complete(@NonNull CompletableFuture<TBPlayer> future, @NonNull TBPlayer player) {
        tb.getGeneralThreads().schedule(() -> future.complete(player), 40, TimeUnit.MILLISECONDS);
    }

    /**
     * Configures {@link TBPlayer} to automatically upload new attributes when the map is updated
     *
     * @param player {@link TBPlayer}
     * @return {@link TBPlayer}
     */
    private TBPlayer handleEvent(@NonNull TBPlayer player) {
        EventHashMap<String, Object> eventMap = (EventHashMap<String, Object>) player.getAttributes();
        eventMap.onEditEvent(att -> tb.getNetworkManager().send(new S2CUpdateAttributeID(player)));
        return player;
    }
}
