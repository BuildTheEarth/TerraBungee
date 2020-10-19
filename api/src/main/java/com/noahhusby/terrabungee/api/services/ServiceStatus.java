package com.noahhusby.terrabungee.api.services;

public enum ServiceStatus {
    DISCARDED(0), RESTARTING(1), LOST_CONNECTION(2), ONLINE(3);

    private final int value;

    ServiceStatus(int value) {
        this.value = value;
    }

    public int getValue() { return value; }
}
