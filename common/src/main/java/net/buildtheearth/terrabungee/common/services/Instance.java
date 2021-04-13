/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - Instance.java
 */

package net.buildtheearth.terrabungee.common.services;

import lombok.Getter;

public class Instance extends TerraBungeeService {
    public static final ServiceType type = ServiceType.INSTANCE;

    @Getter
    private String address;
    @Getter
    private boolean online;
    @Getter
    private boolean running;
    @Getter
    private String template;
    @Getter
    private InstanceType instanceType;

    public Instance(String id, String address, boolean online, boolean running, String template, String status, InstanceType type) {
        super(id);
        this.address = address;
        this.online = online;
        this.running = running;
        this.template = template;
        this.instanceType = type;
        this.setStatus(ServiceStatus.valueOf(status));
    }

    /**
     * Checks if this instance actually exists on the network.
     * This method is required because instances can be deleted by other services on the network.
     * NOTE: always returns false if this instance is a static remote instance, because there is no guarantee that static remote instances actually exist.
     *
     * @return false
     */
    public boolean exists() {
        return false;
    }

    @Override
    public String toString() {
        String addr = getAddress();
        if (addr != null) {
            return "Instance " + getId() + " on address " + address;
        } else {
            return "Instance " + getId();
        }
    }

    @Override
    public ServiceType getType() {
        return type;
    }

    public enum InstanceType {
        STATIC, DYNAMIC
    }
}
