/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - ControllerConnectedEvent.java
 */

package net.buildtheearth.terrabungee.client.events.controller;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ControllerConnectedEvent extends Event {
    public ControllerConnectedEvent(@NonNull TerraBungeeClient tb) {
        super(tb);
    }
}
