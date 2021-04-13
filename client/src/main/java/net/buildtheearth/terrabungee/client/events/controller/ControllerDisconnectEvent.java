/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - ControllerDisconnectEvent.java
 */

package net.buildtheearth.terrabungee.client.events.controller;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.Event;

import javax.annotation.Nonnull;

public class ControllerDisconnectEvent extends Event {

    private final DisconnectReason reason;

    public ControllerDisconnectEvent(@Nonnull TerraBungeeClient tb, DisconnectReason reason) {
        super(tb);
        this.reason = reason;
    }

    public DisconnectReason getReason() {
        return reason;
    }
}
