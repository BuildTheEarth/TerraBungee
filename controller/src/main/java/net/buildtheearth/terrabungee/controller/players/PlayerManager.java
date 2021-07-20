package net.buildtheearth.terrabungee.controller.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.noahhusby.lib.data.storage.StorageHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.common.players.PunishmentEditAction;
import net.buildtheearth.terrabungee.common.players.PunishmentHistory;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SPlayerJoinEventPacket;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SPlayerQuitEventPacket;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.network.proxy.C2PProxyBanDisconnectPacket;
import net.buildtheearth.terrabungee.controller.network.proxy.C2PProxyKickDisconnectPacket;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;
import net.buildtheearth.terrabungee.controller.util.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public void ban(@NonNull UUID staff, @NonNull UUID player, LocalDateTime end, @NonNull String reason) {
        int punishmentId = generatePunishmentId();
        Punishment punishment = new Punishment(punishmentId, Punishment.Type.BAN, staff, player, LocalDateTime.now(), end, reason, Lists.newArrayList(new PunishmentHistory(staff, PunishmentHistory.Type.CREATION, LocalDateTime.now(), new JsonObject())));
        punishments.put(punishmentId, punishment);
        updatePunishmentCache();
        TBPlayer tbPlayer = onlinePlayerRegistry.get(player);
        if(tbPlayer != null && tbPlayer.getProxy() != null) {
            NetworkManager.getInstance().send(new C2PProxyBanDisconnectPacket(tbPlayer.getProxy(), punishment));
        }
    }

    /**
     * Mutes a player
     *
     * @param staff UUID of staff
     * @param player UUID of player
     * @param end End date of punishment
     * @param reason Reason for punishment
     */
    public void mute(@NonNull UUID staff, @NonNull UUID player, LocalDateTime end, @NonNull String reason) {
        int punishmentId = generatePunishmentId();
        Punishment punishment = new Punishment(punishmentId, Punishment.Type.MUTE, staff, player, LocalDateTime.now(), end, reason, Lists.newArrayList(new PunishmentHistory(staff, PunishmentHistory.Type.CREATION, LocalDateTime.now(), new JsonObject())));
        punishments.put(punishmentId, punishment);
        updatePunishmentCache();
        TBPlayer tbPlayer = onlinePlayerRegistry.get(player);
        if(tbPlayer != null && tbPlayer.getProxy() != null) {
            NetworkManager.getInstance().send(new C2PProxyBanDisconnectPacket(tbPlayer.getProxy(), punishment));
        }
    }

    /**
     * Kicks a player
     *
     * @param staff UUID of staff
     * @param player UUID of player
     * @param reason Reason for punishment
     */
    public void kick(@NonNull UUID staff, @NonNull UUID player, @NonNull String reason) {
        if(!getPlayers().containsKey(player) || !getPlayers().get(player).isOnline()) {
            return;
        }
        int punishmentId = generatePunishmentId();
        Punishment punishment = new Punishment(punishmentId, Punishment.Type.KICK, staff, player, LocalDateTime.now(), LocalDateTime.now(), reason, Lists.newArrayList(new PunishmentHistory(staff, PunishmentHistory.Type.CREATION, LocalDateTime.now(), new JsonObject())));
        punishments.put(punishmentId, punishment);
        updatePunishmentCache();
        TBPlayer tbPlayer = onlinePlayerRegistry.get(player);
        if(tbPlayer != null && tbPlayer.getProxy() != null) {
            NetworkManager.getInstance().send(new C2PProxyKickDisconnectPacket(tbPlayer.getProxy(), punishment));
        }
    }

    public void editPunishment(int id, PunishmentEditAction action, JsonObject data) {
        Punishment punishment = getPunishments().get(id);
        UUID staff = UUID.fromString(data.get("staff").getAsString());
        if(punishment == null) {
            return;
        }
        if(action == PunishmentEditAction.REASON) {
            String reason = data.get("reason").getAsString();
            JsonObject historyData = new JsonObject();
            historyData.addProperty("old", punishment.getReason());
            historyData.addProperty("new", reason);
            punishment.setReason(reason);
            punishment.getHistory().add(new PunishmentHistory(staff, PunishmentHistory.Type.EDIT_REASON, LocalDateTime.now(), historyData));
        } else if(action == PunishmentEditAction.END) {
            long length = data.get("length").getAsLong();
            JsonObject historyData = new JsonObject();
            historyData.addProperty("old", punishment.getEnd() == null ? null : punishment.getEnd().toString());
            LocalDateTime end = punishment.getStart().plusSeconds(length == 0 ? 0 : length / 1000);
            historyData.addProperty("new", end.toString());
            punishment.setEnd(end);
            punishment.getHistory().add(new PunishmentHistory(staff, PunishmentHistory.Type.EDIT_TIME, LocalDateTime.now(), historyData));
        } else if(action == PunishmentEditAction.DEACTIVATE) {
            if(punishment.isActive()) {
                JsonObject historyData = new JsonObject();
                historyData.addProperty("old", punishment.getEnd() == null ? null : punishment.getEnd().toString());
                LocalDateTime end = LocalDateTime.now();
                historyData.addProperty("new", end.toString());
                punishment.setEnd(end);
                punishment.getHistory().add(new PunishmentHistory(staff, PunishmentHistory.Type.DEACTIVATE, LocalDateTime.now(), historyData));
            }
        }
        punishments.put(id, punishment);
        updatePunishmentCache();
        punishments.saveAsync();
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
        Gson punishmentGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer()).create();
        punishments.setGson(punishmentGson);
        TerraBungeeController.getInstance().getGeneralThreads().schedule(() -> {
            //ban(UUID.randomUUID(), UUID.fromString("4cfa7dc1-3021-42b0-969b-224a9656cc6d"), null, "Ahhddddhaha");
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
