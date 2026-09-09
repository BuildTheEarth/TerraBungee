/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - Event.java
 */

package net.buildtheearth.terrabungee.client.events;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;

import javax.annotation.Nonnull;

public abstract class Event {
    protected final TerraBungeeClient tb;

    public Event(@Nonnull TerraBungeeClient tb) {
        this.tb = tb;
    }

    @Nonnull
    public TerraBungeeClient getTerraBungee() {
        return tb;
    }
}
