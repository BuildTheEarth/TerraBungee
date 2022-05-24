/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - ServiceIntent.java
 */

package net.buildtheearth.terrabungee.common.services;

/**
 * An enumeration of intentions for services.
 *
 * @author Noah Husby
 */
public enum ServiceIntent {
    INSTANCE_UPDATE,
    PROXY_UPDATE,
    ONLINE_PLAYER_UPDATE,
    EVENT_PLAYER_JOIN_QUIT,

    /**
     * Keeps service updated with muted user data.
     */
    MUTE_CACHE
}
