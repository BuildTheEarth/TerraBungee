package net.buildtheearth.terrabungee.client;

import net.buildtheearth.terrabungee.common.players.TBPlayer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OnlinePlayerCacheTest {

    @Test
    void reconcilesOneThousandPlayersInOnePassAndRemovesStaleEntries() {
        List<TBPlayer> players = new ArrayList<>();
        for (int i = 0; i < 1_000; i++) {
            players.add(player(new UUID(0, i + 1), "Player" + i));
        }
        players.add(players.get(0));
        AtomicInteger initialized = new AtomicInteger();

        Map<UUID, TBPlayer> result = OnlinePlayerCache.reconcile(players, player -> {
            initialized.incrementAndGet();
            return player;
        });

        assertEquals(1_000, result.size());
        assertEquals(1_000, initialized.get());
        assertFalse(result.containsKey(new UUID(0, 2_000)));
    }

    private static TBPlayer player(UUID uuid, String name) {
        return new TBPlayer(uuid, name, null, null, 0, "Hub1", true, "ProxyServer1");
    }
}
