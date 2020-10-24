

package com.noahhusby.terrabungee.api;

import com.noahhusby.terrabungee.api.services.Instance;

import java.util.ArrayList;
import java.util.List;

public class InstanceManager {

    private List<Instance> instances = new ArrayList<>();
    private final TerraBungee terraBungee;

    protected InstanceManager(TerraBungee terraBungee) {
        this.terraBungee = terraBungee;

    }

    public List<Instance> getInstances() {
        return instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }
}
