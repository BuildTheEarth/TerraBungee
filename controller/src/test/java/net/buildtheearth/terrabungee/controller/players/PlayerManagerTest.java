package net.buildtheearth.terrabungee.controller.players;

import net.buildtheearth.api.players.ControllerPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PlayerManagerTest {
    @Test
    void findsPlayerByLinkedDiscordId() {
        ControllerPlayer first = player("111");
        ControllerPlayer second = player("222");

        assertSame(second, PlayerManager.findPlayerByDiscordId(List.of(first, second), "222"));
    }

    @Test
    void returnsNullForUnknownOrInvalidDiscordId() {
        ControllerPlayer player = player("111");

        assertNull(PlayerManager.findPlayerByDiscordId(List.of(player), "999"));
        assertNull(PlayerManager.findPlayerByDiscordId(List.of(player), " "));
        assertNull(PlayerManager.findPlayerByDiscordId(List.of(player), null));
    }

    private static ControllerPlayer player(String discordId) {
        ControllerPlayer player = new ControllerPlayer(UUID.randomUUID());
        player.setDiscordId(discordId);
        return player;
    }
}
