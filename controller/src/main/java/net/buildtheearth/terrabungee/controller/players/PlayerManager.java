package net.buildtheearth.terrabungee.controller.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import com.noahhusby.lib.data.storage.StorageHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.common.players.PunishmentHistory;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SPlayerJoinEventPacket;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SPlayerQuitEventPacket;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Noah Husby
 */
public class PlayerManager implements Module {
    private static PlayerManager instance = null;

    public static PlayerManager getInstance() {
        return instance == null ? instance = new PlayerManager() : instance;
    }

    private final ExecutorService manipulationThread = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("player-manipulation-%d").build());

    @Getter
    private final StorageHashMap<UUID, ControllerPlayer> players = new StorageHashMap<>(UUID.class, ControllerPlayer.class);

    @Getter
    private final StorageHashMap<Integer, Punishment> punishments = new StorageHashMap<>(Integer.class, Punishment.class);
    private Map<UUID, List<Punishment>> punishmentsByUuid = Maps.newHashMap();

    private Map<UUID, ControllerPlayer> onlinePlayerRegistry = Maps.newHashMap();

    public Map<UUID, ControllerPlayer> getOnlinePlayerRegistry() {
        return onlinePlayerRegistry;
    }

    public int getTotalPlayers() {
        return players.size();
    }

    public int getTotalOnlinePlayers() {
        return onlinePlayerRegistry.size();
    }

    public void proxyPlayerDrop(String id, List<ControllerPlayer> playerDrop) {
        manipulate(() -> {
            Map<UUID, ControllerPlayer> playerJoinQuitMap = Maps.newHashMap();
            for(Map.Entry<UUID, ControllerPlayer> e : ImmutableMap.copyOf(players).entrySet()) {
                ControllerPlayer player = e.getValue();
                if(player.getProxy() != null && player.getProxy().equals(id)) {
                    player.setOnline(false);
                    playerJoinQuitMap.put(e.getKey(), e.getValue());
                }
            }

            for (ControllerPlayer p : playerDrop) {
                ControllerPlayer player = players.putIfAbsent(p.getUniqueID(), p);
                if(player == null) {
                    players.put(p.getUniqueID(), p);
                    return;
                }

                player.setName(p.getName());
                player.setServer(p.getServer());
                player.setOnline(true);
                player.setProxy(id);

                if (!playerJoinQuitMap.containsKey(p.getUniqueID())) {
                    ServiceManager.getInstance().runIntentAction(ServiceIntent.EVENT_PLAYER_JOIN_QUIT, service -> NetworkManager.getInstance().send(new C2SPlayerJoinEventPacket(player, service)));
                }

                playerJoinQuitMap.remove(p.getUniqueID());
            }

            for (ControllerPlayer p : playerJoinQuitMap.values()) {
                ServiceManager.getInstance().runIntentAction(ServiceIntent.EVENT_PLAYER_JOIN_QUIT, service -> NetworkManager.getInstance().send(new C2SPlayerQuitEventPacket(p, service)));
            }
        });
    }

    public void updateAttribute(UUID uuid, Map<String, Object> attributes) {
        manipulate(() -> {
            ControllerPlayer player = players.get(uuid);
            if(player != null) {
                player.setAttributes(attributes);
                //players.saveAsync();
            }
        });
    }

    private void manipulate(Runnable runnable) {
        manipulationThread.submit(runnable);
        manipulationThread.submit(() -> {
            Map<UUID, ControllerPlayer> online = Maps.newHashMap();

            for(ControllerPlayer p : ImmutableList.copyOf(players.values())) {
                if(p.isOnline()) {
                    online.put(p.getUniqueID(), p);
                }
            }

            onlinePlayerRegistry = ImmutableMap.copyOf(online);
        });
    }

    /**
     * Bans a player
     *
     * @param staff UUID of staff
     * @param player UUID of player
     * @param end End date of punishment
     * @param reason Reason for punishment
     */
    public void ban(@NonNull UUID staff, @NonNull UUID player, Date end, @NonNull String reason) {
        int punishmentId = generatePunishmentId();
        punishments.put(punishmentId, new Punishment(punishmentId, Punishment.Type.BAN, staff, player, new Date(), end, reason, Lists.newArrayList(new PunishmentHistory(staff, PunishmentHistory.Type.CREATION, new Date(), new JsonObject()))));
        updatePunishmentCache();
    }

    /**
     * Gets a list of punishments for a player
     *
     * @param uuid UUID of player
     * @return List of punishments
     */
    public List<Punishment> getPunishmentsByPlayer(UUID uuid) {
        return punishmentsByUuid.get(uuid);
    }

    /*
     * Internal Methods
     */

    private int generatePunishmentId() {
        int id = punishments.size();
        while(punishments.containsKey(id)) {
            id++;
        }
        return id;
    }

    private void updatePunishmentCache() {
        Map<UUID, List<Punishment>> tempCache = Maps.newHashMap();
        for(Punishment punishment : punishments.values()) {
            if(!tempCache.containsKey(punishment.getPlayer())) {
                tempCache.put(punishment.getPlayer(), Lists.newArrayList(punishment));
            } else {
                tempCache.get(punishment.getPlayer()).add(punishment);
            }
        }
        punishmentsByUuid = ImmutableMap.copyOf(tempCache);
    }

    @Override
    public void onEnable() {
        TerraBungeeController.getInstance().getGeneralThreads().schedule(new Runnable() {
            @Override
            public void run() {
                ban(UUID.randomUUID(), UUID.randomUUID(), null, "Ahhddddhaha");
            }
        }, 5, TimeUnit.SECONDS);
        punishments.onLoadEvent(this::updatePunishmentCache);
        punishments.onSaveEvent(this::updatePunishmentCache);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getModuleName() {
        return "Players";
    }
}
