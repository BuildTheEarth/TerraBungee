/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - InstanceUpdateEvent.java
 */

package net.buildtheearth.terrabungee.client.events.service;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.Event;
import net.buildtheearth.terrabungee.common.instance.Instance;

import java.util.List;

public class InstanceUpdateEvent extends Event {
    private final List<Instance> instances;

    public InstanceUpdateEvent(TerraBungeeClient tb, List<Instance> instances) {
        super(tb);
        this.instances = instances;
    }

    public List<Instance> getInstances() {
        return instances;
    }
}
