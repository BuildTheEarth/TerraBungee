/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - InstanceUpdateEvent.java
 */

package net.buildtheearth.terrabungee.client.events.player;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.Event;
import net.buildtheearth.terrabungee.common.players.TBPlayer;

public class PlayerJoinEvent extends Event {
    private final TBPlayer player;

    public PlayerJoinEvent(TerraBungeeClient tb, TBPlayer player) {
        super(tb);
        this.player = player;
    }

    public TBPlayer getPlayer() {
        return player;
    }
}
