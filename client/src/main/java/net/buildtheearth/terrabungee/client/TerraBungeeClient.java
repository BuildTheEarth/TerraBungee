/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - TerraBungee.java
 */

package net.buildtheearth.terrabungee.client;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.buildtheearth.terrabungee.client.events.EventListener;
import net.buildtheearth.terrabungee.client.network.S2C.S2CServiceMessagePacket;
import net.buildtheearth.terrabungee.client.util.TBStats;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;
import net.buildtheearth.terrabungee.common.services.ServiceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class TerraBungeeClient {

    private final ExecutorService eventThreads = TerraBungeeUtil.newThreadPoolExecutor(16, "terrabungee-event");
    @Getter(AccessLevel.PROTECTED)
    private final ScheduledExecutorService generalThreads = TerraBungeeUtil.newThreadPoolScheduledExecutor(8, "terrabungee");
    @Getter
    @Setter
    private Logger logger = Logger.getLogger("TerraBungee");

    @Getter
    private final NetworkManager networkManager;
    @Getter
    private final PlayerManager playerManager;
    @Getter
    private final InstanceManager instanceManager;
    @Getter
    private final FleetManager fleetManager;
    @Getter
    private final ServiceType serviceType;
    @Getter
    private final String Id;

    @Getter
    private boolean discarded = false;

    @Getter
    private List<ServiceIntent> intents = new ArrayList<>();
    @Getter
    @Setter
    private TBStats stats = new TBStats("UNKNOWN", 0, 0, 0, 0);

    protected TerraBungeeClient(@NonNull ServiceType serviceType, @NonNull String Id, @NonNull String controller) {
        this.serviceType = serviceType;
        this.Id = Id;
        this.networkManager = new NetworkManager(controller, this);
        this.instanceManager = new InstanceManager(this);
        this.fleetManager = new FleetManager(this);
        this.playerManager = new PlayerManager(this);
    }

    /**
     * Attempts the connection process to the controller
     */
    public void connect() {
        getNetworkManager().connect();
        discarded = false;
    }

    /**
     * Disconnects the service from the controller
     */
    public void disconnect() {
        getNetworkManager().disconnect();
    }

    /**
     * Sets whether the API should automatically reconnect to the controller after loosing connection
     *
     * @param reconnect True if the API should automatically reconnect, false if not
     */
    public void setAutoReconnect(boolean reconnect) {
        getNetworkManager().setAutoReconnect(reconnect);
    }

    /**
     * Add listener to TerraBungee
     *
     * @param e {@link EventListener}
     */
    public void addListener(@NonNull EventListener e) {
        networkManager.addListener(e);
    }

    /**
     * Remove listener from TerraBungee
     *
     * @param e {@link EventListener}
     */
    public void removeListener(@NonNull EventListener e) {
        networkManager.removeListener(e);
    }

    /**
     * Get a list of listeners
     *
     * @return {@link List<EventListener>}
     */
    public List<EventListener> getListeners() {
        return networkManager.getListeners();
    }

    /**
     * Enable a specific intent
     * <p>
     * Read more: https://github.com/BuildTheEarth/TerraBungeeAPI/blob/main/docs/intents.md
     *
     * @param intent {@link ServiceIntent}
     */
    public void enableIntent(@NonNull ServiceIntent intent) {
        if (!intents.contains(intent)) {
            intents.add(intent);
        }
    }

    /**
     * Enable specific intents
     * <p>
     * Read More: https://github.com/BuildTheEarth/TerraBungeeAPI/blob/main/docs/intents.md
     *
     * @param intents {@link ServiceIntent}
     */
    public void enableIntents(@NonNull ServiceIntent... intents) {
        for (int x = 0; x < intents.length; x++) {
            enableIntent(intents[x]);
        }
    }

    /**
     * Disable a specific intent
     * <p>
     * Read more: https://github.com/BuildTheEarth/TerraBungeeAPI/blob/main/docs/intents.md
     *
     * @param intent {@link ServiceIntent}
     */
    public void disableIntent(@NonNull ServiceIntent intent) {
        intents.remove(intent);
    }

    /**
     * Disable specific intents
     * <p>
     * Read more: https://github.com/BuildTheEarth/TerraBungeeAPI/blob/main/docs/intents.md
     *
     * @param intents {@link ServiceIntent}
     */
    public void disableIntents(@NonNull ServiceIntent... intents) {
        for (int x = 0; x < intents.length; x++) {
            disableIntent(intents[x]);
        }
    }

    /**
     * Sends a message to another service using the service messaging channel
     *
     * @param service Service ID
     * @param object  Message
     * @return Response from controller
     */
    public CompletableFuture<Response> sendMessage(@NonNull String service, @NonNull JsonObject object) {
        return networkManager.send(new S2CServiceMessagePacket(service, object));
    }

    /**
     * Get player by username
     *
     * @param name Username
     * @return {@link TBPlayer}
     */
    public CompletableFuture<TBPlayer> getPlayer(@NonNull String name) {
        return playerManager.getPlayer(name);
    }

    /**
     * Get player by uuid
     *
     * @param uuid UUID
     * @return {@link TBPlayer}
     */
    public CompletableFuture<TBPlayer> getPlayer(@NonNull UUID uuid) {
        return playerManager.getPlayer(uuid);
    }

    /**
     * Gets list of online players from local cache
     *
     * @return List of {@link TBPlayer}
     */
    public CompletableFuture<Map<UUID, TBPlayer>> getOnlinePlayers() {
        return playerManager.getOnlinePlayers();
    }

    /**
     * Get a cached online player by UUID
     *
     * @param uuid {@link UUID}
     * @return {@link TBPlayer}
     */
    public TBPlayer getCachedOnlinePlayer(@NonNull UUID uuid) {
        return playerManager.getCachedOnlinePlayer(uuid);
    }

    /**
     * Get a cached online player by name
     *
     * @param name Username of player
     * @return {@link TBPlayer}
     */
    public TBPlayer getCachedOnlinePlayer(@NonNull String name) {
        return playerManager.getCachedOnlinePlayer(name);
    }

    /**
     * Get map of cached online players
     *
     * @return Map of cached online players
     */
    public Map<UUID, TBPlayer> getCachedOnlinePlayers() {
        return playerManager.getCachedOnlinePlayers();
    }

    public void triggerEvent(@NonNull Consumer<EventListener> listener) {
        eventThreads.submit(() -> getListeners().forEach(listener));
    }
}
