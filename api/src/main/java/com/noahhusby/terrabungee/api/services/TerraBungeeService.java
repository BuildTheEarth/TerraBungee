package com.noahhusby.terrabungee.api.services;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.ServiceIntent;

import java.util.ArrayList;
import java.util.List;

public abstract class TerraBungeeService implements ITerraBungeeService {

    private final String Id;

    private ServiceStatus status = ServiceStatus.DISCARDED;
    private Object client;
    private long lastAlive = -1;

    private List<ServiceIntent> intents = new ArrayList<>();

    public TerraBungeeService(String Id) {
        this.Id = Id;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public void setStatus(ServiceStatus s) {
        this.status = s;
    }

    @Override
    public ServiceStatus getStatus() {
        if(lastAlive + Constants.serviceTimeout < System.currentTimeMillis() && status == ServiceStatus.ONLINE)
            status = ServiceStatus.LOST_CONNECTION;
        return status;
    }

    @Override
    public void setClient(Object client) {
        this.client = client;
    }

    @Override
    public Object getClient() {
        return client;
    }

    public void keepAlive() {
        this.lastAlive = System.currentTimeMillis();
        this.status = ServiceStatus.ONLINE;
    }

    public List<ServiceIntent> getIntents() {
        return intents;
    }

    public void setIntents(List<ServiceIntent> intents) {
        this.intents = intents;
    }
}
