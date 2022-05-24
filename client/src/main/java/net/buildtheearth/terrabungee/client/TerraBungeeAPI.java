/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - TerraBungeeAPI.java
 */

package net.buildtheearth.terrabungee.client;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.net.InetSocketAddress;

/**
 * @author Noah Husby
 */
@UtilityClass
public class TerraBungeeAPI {
    /**
     * Create a new TerraBungee service
     *
     * @param Id         Unique ID of Service
     * @param controller IP of Controller
     * @return {@link TerraBungeeClient}
     */
    public static TerraBungeeClient createService(@NonNull String Id, @NonNull InetSocketAddress controller) {
        return new TerraBungeeClient(Id, controller);
    }
}
