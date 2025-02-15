/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - Proxy.java
 */

package net.buildtheearth.terrabungee.common.services;

public class Custom extends TerraBungeeService {
    public static final ServiceType type = ServiceType.CUSTOM;

    public Custom(String Id) {
        super(Id);
    }

    @Override
    public ServiceType getType() {
        return type;
    }
}
