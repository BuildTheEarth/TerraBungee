/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - Event.java
 */

package net.buildtheearth.terrabungee.client.events;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import org.jspecify.annotations.NonNull;


public abstract class Event {
    protected final TerraBungeeClient tb;

    public Event(@NonNull TerraBungeeClient tb) {
        this.tb = tb;
    }

    @NonNull
    public TerraBungeeClient getTerraBungee() {
        return tb;
    }
}
