/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - PlayerManager.java
 */

package net.buildtheearth.terrabungee.client;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrieveUncachedPlayerPacket;
import net.buildtheearth.terrabungee.client.network.S2C.S2CUpdateAttributeID;
import net.buildtheearth.terrabungee.client.util.Manager;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.util.EventHashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Noah Husby
 */
public class PlayerManager extends Manager {

    protected PlayerManager(TerraBungeeClient tb) {
        super(tb);
    }

    private volatile Map<UUID, TBPlayer> onlinePlayers = Map.of();
    private final Map<UUID, CompletableFuture<TBPlayer>> pendingUuidLookups = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<TBPlayer>> pendingNameLookups = new ConcurrentHashMap<>();

    /**
     * Get player by username
     *
     * @param name Username
     * @return {@link TBPlayer}
     */
    public CompletableFuture<TBPlayer> getPlayer(@NonNull String name) {
        for (TBPlayer player : onlinePlayers.values()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return completed(player);
            }
        }

        String lookupKey = name.toLowerCase(Locale.ROOT);
        CompletableFuture<TBPlayer> lookup = pendingNameLookups.computeIfAbsent(
                lookupKey, ignored -> retrieveUncachedPlayer(name));
        lookup.whenComplete((player, throwable) -> pendingNameLookups.remove(lookupKey, lookup));
        return lookup;
    }

    /**
     * Get player by uuid
     *
     * @param uuid UUID
     * @return {@link TBPlayer}
     */
    public CompletableFuture<TBPlayer> getPlayer(@NonNull UUID uuid) {
        TBPlayer player = onlinePlayers.get(uuid);
        if (player != null) {
            return completed(player);
        }

        CompletableFuture<TBPlayer> lookup = pendingUuidLookups.computeIfAbsent(
                uuid, ignored -> retrieveUncachedPlayer(uuid.toString()));
        lookup.whenComplete((resolved, throwable) -> pendingUuidLookups.remove(uuid, lookup));
        return lookup;
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
        onlinePlayers = OnlinePlayerCache.reconcile(players, this::handleEvent);
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
        for (TBPlayer p : onlinePlayers.values()) {
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
        return ImmutableMap.copyOf(onlinePlayers);
    }

    private CompletableFuture<TBPlayer> completed(@NonNull TBPlayer player) {
        return CompletableFuture.completedFuture(player);
    }

    private CompletableFuture<TBPlayer> retrieveUncachedPlayer(String identifier) {
        return tb.getNetworkManager().send(new S2CRetrieveUncachedPlayerPacket(identifier))
                .handle((response, throwable) -> {
                    if (throwable != null || response == null || response.getData() == null
                            || response.getCode() != Response.ResponseCode.SUCCESS) {
                        return null;
                    }
                    return handleEvent(TerraBungeeUtil.GSON.fromJson(response.getData(), TBPlayer.class));
                });
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
