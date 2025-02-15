/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - Proxy.java
 */

package net.buildtheearth.terrabungee.common.services;

public class Proxy extends TerraBungeeService {
    public static final ServiceType type = ServiceType.PROXY;

    public Proxy(String Id) {
        super(Id);
    }

    @Override
    public ServiceType getType() {
        return type;
    }
}
