/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - Proxy.java
 */

package net.buildtheearth.terrabungee.common.services;

public class Node extends Service {
    public static final ServiceType type = ServiceType.NODE;

    public Node(String Id) {
        super(Id);
    }

    @Override
    public ServiceType getType() {
        return type;
    }
}
