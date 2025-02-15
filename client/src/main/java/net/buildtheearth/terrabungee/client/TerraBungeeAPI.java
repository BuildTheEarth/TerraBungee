/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - TerraBungeeAPI.java
 */

package net.buildtheearth.terrabungee.client;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.buildtheearth.terrabungee.common.services.ServiceType;

/**
 * @author Noah Husby
 */
@UtilityClass
public class TerraBungeeAPI {
    /**
     * Create a new TerraBungee service
     *
     * @param serviceType {@link ServiceType}
     * @param Id          Unique ID of Service
     * @param controller  IP of Controller
     * @return {@link TerraBungeeClient}
     */
    public static TerraBungeeClient createService(@NonNull ServiceType serviceType, @NonNull String Id, @NonNull String controller) {
        return new TerraBungeeClient(serviceType, Id, controller);
    }
}
