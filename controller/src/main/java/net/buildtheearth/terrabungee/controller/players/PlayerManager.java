package net.buildtheearth.terrabungee.controller.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noahhusby.lib.data.storage.StorageHashMap;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SPlayerJoinEventPacket;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SPlayerQuitEventPacket;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private final StorageHashMap<UUID, ControllerPlayer> players = new StorageHashMap<>(UUID.class, ControllerPlayer.class);

    private Map<UUID, ControllerPlayer> onlinePlayerRegistry = Maps.newHashMap();

    public StorageHashMap<UUID, ControllerPlayer> getPlayers() {
        return players;
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

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getModuleName() {
        return "Players";
    }
}
