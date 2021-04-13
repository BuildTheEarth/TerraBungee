/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - InstanceUpdateEvent.java
 */

package net.buildtheearth.terrabungee.client.events.player;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.Event;
import net.buildtheearth.terrabungee.common.players.TBPlayer;

import java.util.List;

public class OnlineCacheHitEvent extends Event {
    private final List<TBPlayer> players;

    public OnlineCacheHitEvent(TerraBungeeClient tb, List<TBPlayer> players) {
        super(tb);
        this.players = players;
    }

    public List<TBPlayer> getPlayers() {
        return players;
    }
}
