package com.noahhusby.terrabungee.api.services;

public enum ServiceStatus {
    DISCARDED(0), AWAIT_INIT(1), RESTARTING(2), LOST_CONNECTION(3), ONLINE(4), STATIC(5);

    private final int value;

    ServiceStatus(int value) {
        this.value = value;
    }

    public int getValue() { return value; }
}
