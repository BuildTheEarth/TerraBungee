/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - EventAdapter.java
 */

package net.buildtheearth.terrabungee.client.events;

import net.buildtheearth.terrabungee.client.events.controller.ControllerConnectedEvent;
import net.buildtheearth.terrabungee.client.events.controller.ControllerDisconnectEvent;
import net.buildtheearth.terrabungee.client.events.message.ServiceMessageEvent;
import net.buildtheearth.terrabungee.client.events.player.OnlineCacheHitEvent;
import net.buildtheearth.terrabungee.client.events.player.PlayerJoinEvent;
import net.buildtheearth.terrabungee.client.events.player.PlayerQuitEvent;
import net.buildtheearth.terrabungee.client.events.service.InstanceUpdateEvent;
import net.buildtheearth.terrabungee.client.events.service.ServiceReconnectEvent;

public abstract class EventListener {
    public void onServiceMessage(ServiceMessageEvent event) {
    }

    public void onControllerConnect(ControllerConnectedEvent event) {
    }

    public void onControllerDisconnect(ControllerDisconnectEvent event) {
    }

    public void onServiceReconnect(ServiceReconnectEvent event) {
    }

    public void onInstanceUpdate(InstanceUpdateEvent event) {
    }

    public void onOnlineCacheHit(OnlineCacheHitEvent event) {
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
    }
}
