/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - TerraBungeeService.java
 */

package net.buildtheearth.terrabungee.common.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.buildtheearth.terrabungee.common.TerraBungeeVersion;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Service {

    @Getter
    private final String id;

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
}
