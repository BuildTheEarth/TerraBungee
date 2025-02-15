/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - Proxy.java
 */

package net.buildtheearth.terrabungee.common.services;

public class Node extends TerraBungeeService {
    public static final ServiceType type = ServiceType.NODE;

    public Node(String Id) {
        super(Id);
    }

    @Override
    public ServiceType getType() {
        return type;
    }
}
