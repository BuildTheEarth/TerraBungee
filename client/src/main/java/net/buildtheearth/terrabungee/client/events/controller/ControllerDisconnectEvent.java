/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - ControllerDisconnectEvent.java
 */

package net.buildtheearth.terrabungee.client.events.controller;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.Event;
import org.jspecify.annotations.NonNull;

public class ControllerDisconnectEvent extends Event {

    private final DisconnectReason reason;

    public ControllerDisconnectEvent(@NonNull TerraBungeeClient tb, DisconnectReason reason) {
        super(tb);
        this.reason = reason;
    }

    public DisconnectReason getReason() {
        return reason;
    }
}
