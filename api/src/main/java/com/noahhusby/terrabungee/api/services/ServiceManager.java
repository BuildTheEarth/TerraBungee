package com.noahhusby.terrabungee.api.services;

import java.util.ArrayList;
import java.util.List;

public class ServiceManager {
    private static ServiceManager instance;

    public static ServiceManager getInstance() {
        if(instance == null) instance = new ServiceManager();
        return instance;
    }

    private ServiceManager() {}

    private List<Instance> instances = new ArrayList<>();

    public List<Instance> getInstances() {
        return instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }
}
