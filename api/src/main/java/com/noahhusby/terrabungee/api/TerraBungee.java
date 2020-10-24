package com.noahhusby.terrabungee.api;

import com.noahhusby.terrabungee.api.services.ServiceType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TerraBungee {

    private List<ServiceIntent> activeIntents = new ArrayList<>();

    private final NetworkManager networkManager;
    private final InstanceManager instanceManager;
    private final ScheduledExecutorService executorService;
    private final ServiceType serviceType;
    private final String Id;

    public TerraBungee(ServiceType serviceType, String Id, String controller) {
        this(serviceType, Id, controller, Executors.newScheduledThreadPool(2));
    }

    public TerraBungee(ServiceType serviceType, String Id, String controller, ScheduledExecutorService executorService) {
        this.executorService = executorService;
        this.serviceType = serviceType;
        this.Id = Id;
        this.networkManager = new NetworkManager(controller, this);
        this.instanceManager = new InstanceManager(this);
    }

    public String getId() {
        return Id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public InstanceManager getInstanceManager() {
        return instanceManager;
    }

    public List<ServiceIntent> getIntents() {
        return activeIntents;
    }

    public void enableIntent(ServiceIntent intent) {
        if(!activeIntents.contains(intent))
            activeIntents.add(intent);
    }

    public void enableIntents(ServiceIntent... intents) {
        for(int x = 0; x < intents.length; x++)
            enableIntent(intents[x]);
    }

    public void disableIntent(ServiceIntent intent) {
        activeIntents.remove(intent);
    }

    public void disableIntents(ServiceIntent... intents) {
        for(int x = 0; x < intents.length; x++)
            disableIntent(intents[x]);
    }

    protected ScheduledExecutorService getExecutorService() {
        return executorService;
    }



}
