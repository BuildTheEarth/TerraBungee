/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - TerraBungeeService.java
 */

package net.buildtheearth.terrabungee.common.services;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.buildtheearth.terrabungee.common.TerraBungeeVersion;

import java.util.ArrayList;
import java.util.List;

public abstract class Service {

    @Getter
    private final String Id;

    @Getter
    @Setter
    private ServiceStatus status = ServiceStatus.DISCARDED;

    @Getter
    @Setter
    private Object client;

    @Getter
    @Setter
    private List<ServiceIntent> intents = new ArrayList<>();

    @Getter
    @Setter
    private TerraBungeeVersion version;

    public Service(@NonNull String Id) {
        this.Id = Id;
    }

    /**
     * Gets the type of service
     *
     * @return The type of service
     */
    public abstract ServiceType getType();
}
