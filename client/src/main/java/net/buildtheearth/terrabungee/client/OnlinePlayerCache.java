package net.buildtheearth.terrabungee.client;

import net.buildtheearth.terrabungee.common.players.TBPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;

final class OnlinePlayerCache {

    private OnlinePlayerCache() {
    }

    static Map<UUID, TBPlayer> reconcile(List<TBPlayer> players, UnaryOperator<TBPlayer> initializer) {
        Map<UUID, TBPlayer> updated = new HashMap<>(Math.max(16, players.size() * 2));
        for (TBPlayer player : players) {
            updated.computeIfAbsent(player.getUniqueID(), ignored -> initializer.apply(player));
        }
        return Map.copyOf(updated);
    }
}
