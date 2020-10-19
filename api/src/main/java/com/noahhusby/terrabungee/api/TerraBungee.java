package com.noahhusby.terrabungee.api;

import com.noahhusby.terrabungee.api.services.ServiceType;

import java.util.ArrayList;
import java.util.List;

public class TerraBungee {

    private static TerraBungee instance = null;

    private TerraBungeeNetworkManager networkManager = null;
    private List<ServiceIntent> activeIntents = new ArrayList<>();
    private ServiceType serviceType = ServiceType.NONE;
    private String Id;

    public static TerraBungee getInstance() {
        if(instance == null) instance = new TerraBungee();
        return instance;
    }

    private TerraBungee() { }

    public void createService(String controller, ServiceType serviceType, String Id) {
        this.networkManager = new TerraBungeeNetworkManager(controller);
        this.serviceType = serviceType;
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public TerraBungeeNetworkManager getNetworkManager() {
        return networkManager;
    }

    public List<ServiceIntent> getIntents() {
        return activeIntents;
    }

    public void enableIntent(ServiceIntent intent) {
        if(serviceType == ServiceType.NONE) {
            try {
                throw new InstanceNotServiceException();
            } catch (InstanceNotServiceException e) {
                e.printStackTrace();
            }
        }

        if(!activeIntents.contains(intent))
            activeIntents.add(intent);
    }

    public void enableIntents(ServiceIntent... intents) {
        for(int x = 0; x < intents.length; x++)
            enableIntent(intents[x]);
    }

    public void disableIntent(ServiceIntent intent) {
        if(serviceType == ServiceType.NONE) {
            try {
                throw new InstanceNotServiceException();
            } catch (InstanceNotServiceException e) {
                e.printStackTrace();
            }
        }

        activeIntents.remove(intent);
    }

    public void disableIntents(ServiceIntent... intents) {
        for(int x = 0; x < intents.length; x++)
            disableIntent(intents[x]);
    }



}
