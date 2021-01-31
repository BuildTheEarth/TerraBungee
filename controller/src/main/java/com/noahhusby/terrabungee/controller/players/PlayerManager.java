package com.noahhusby.terrabungee.controller.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noahhusby.lib.data.storage.StorageList;
import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.network.C2S.C2SPlayerJoinEventPacket;
import com.noahhusby.terrabungee.controller.network.C2S.C2SPlayerQuitEventPacket;
import com.noahhusby.terrabungee.controller.network.NetworkManager;
import com.noahhusby.terrabungee.controller.services.ServiceManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Noah Husby
 */
public class PlayerManager {
    private static PlayerManager instance = null;
    public static PlayerManager getInstance() {
        return instance == null ? instance = new PlayerManager() : instance;
    }

    private PlayerManager() {
        // A questionable way to load players into the registry if
        TerraBungeeController.getInstance().getGeneralThreads().schedule(() -> manipulate(players -> {}), 10, TimeUnit.SECONDS);
    }

    private final ExecutorService manipulationThread = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("player-manipulation-%d").build());

    private final StorageList<ControllerPlayer> players = new StorageList<>(ControllerPlayer.class);
    private Map<UUID, ControllerPlayer> playerRegistry = Maps.newHashMap();
    private Map<UUID, ControllerPlayer> onlinePlayerRegistry = Maps.newHashMap();

    public StorageList<ControllerPlayer> getPlayers() {
        return players;
    }

    public Map<UUID, ControllerPlayer> getPlayersRegistry() {
        return playerRegistry;
    }

    public Map<UUID, ControllerPlayer> getOnlinePlayerRegistry() {
        return onlinePlayerRegistry;
    }

    public int getTotalPlayers() {
        return players.size();
    }

    public int getTotalOnlinePlayers() {
        return onlinePlayerRegistry.size();
    }

    public void proxyPlayerDrop(String id, List<ControllerPlayer> players) {
        manipulate(ps -> {
            Map<UUID, ControllerPlayer> playerMap = Maps.newHashMap();
            for(ControllerPlayer p : ps)
                playerMap.put(p.getUniqueID(), p);

            Map<UUID, ControllerPlayer> playerJoinQuitMap = Maps.newHashMap();

            playerMap.forEach((uuid, controllerPlayer) -> {
                if(controllerPlayer.getProxy() != null && controllerPlayer.getProxy().equals(id)) {
                    controllerPlayer.setOnline(false);
                    playerJoinQuitMap.put(uuid, controllerPlayer);
                }
            });

            for(ControllerPlayer p : players) {
                ControllerPlayer player = playerMap.get(p.getUniqueID());
                if(player == null) {
                    ps.add(p);
                    continue;
                }

                player.setName(p.getName());
                player.setServer(p.getServer());
                player.setOnline(true);
                player.setProxy(id);

                if(!playerJoinQuitMap.containsKey(p.getUniqueID())) {
                    ServiceManager.getInstance().runIntentAction(ServiceIntent.EVENT_PLAYER_JOIN_QUIT, service -> NetworkManager.getInstance().send(new C2SPlayerJoinEventPacket(player, service)));
                }

                playerJoinQuitMap.remove(p.getUniqueID());
            }

            for(ControllerPlayer p : playerJoinQuitMap.values()) {
                ServiceManager.getInstance().runIntentAction(ServiceIntent.EVENT_PLAYER_JOIN_QUIT, service -> NetworkManager.getInstance().send(new C2SPlayerQuitEventPacket(p, service)));
            }
        });
    }

    public void updateAttribute(UUID uuid, Map<String, Object> attributes) {
        manipulate(ps -> {
            for(ControllerPlayer p : ps) {
                if(p.getUniqueID().equals(uuid)) {
                    p.setAttributes(attributes);
                    return;
                }
            }
        });
    }

    private void manipulate(Consumer<StorageList<ControllerPlayer>> p) {
        manipulationThread.submit(() -> p.accept(players));
        manipulationThread.submit(() -> {
            Map<UUID, ControllerPlayer> all = Maps.newHashMap();
            Map<UUID, ControllerPlayer> online = Maps.newHashMap();

            for(ControllerPlayer e : ImmutableList.copyOf(players)) {
                if(all.containsKey(e.getUniqueID())) {
                    players.remove(e);
                } else {
                    all.put(e.getUniqueID(), e.deepCopy());
                    if(e.isOnline()) online.put(e.getUniqueID(), e.deepCopy());
                }
            }

            playerRegistry = ImmutableMap.copyOf(all);
            onlinePlayerRegistry = ImmutableMap.copyOf(online);
        });
    }
}
