package com.noahhusby.terrabungee.api.services;

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
