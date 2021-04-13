/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - ServiceReconnectEvent.java
 */

package net.buildtheearth.terrabungee.client.events.service;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.Event;

public class ServiceReconnectEvent extends Event {
    public ServiceReconnectEvent(TerraBungeeClient tb) {
        super(tb);
    }
}
