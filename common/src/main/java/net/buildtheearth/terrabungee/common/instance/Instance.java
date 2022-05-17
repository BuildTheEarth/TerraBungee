/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - Instance.java
 */

package net.buildtheearth.terrabungee.common.instance;

import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class Instance {

    private String id;

    private InetSocketAddress address;

    private InstanceStatus status;

    private String template;

    private InstanceType instanceType;

    @Override
    public String toString() {
        return "Instance " + getId() + " on address " + address;
    }
}
