package com.noahhusby.terrabungee.controller.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import com.noahhusby.lib.data.storage.StorageList;
import com.noahhusby.terrabungee.api.services.*;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import com.noahhusby.terrabungee.controller.discord.embeds.StaticInstanceAddedEmbed;
import com.noahhusby.terrabungee.controller.discord.embeds.StaticInstanceRemovedEmbed;
import lombok.Getter;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InstanceManager {
    private static InstanceManager instance = null;

    public static InstanceManager getInstance() {
        return instance == null ? instance = new InstanceManager() : instance;
    }

    public InstanceManager() {
        TerraBungeeController.getInstance().getGeneralThreads().scheduleAtFixedRate(this::updateInstances, 0, 2, TimeUnit.SECONDS);
    }

    @Getter private final StorageList<StorableStaticInstance> storableStaticInstances = new StorageList<>(StorableStaticInstance.class);

    /**
     * Add a static instance
     * @param service The service that added the instance
     * @param id ID of the instance
     * @param address Address of the instance
     * @return True if successfully added, false if not
     */
    public boolean addStaticInstance(ITerraBungeeService service, String id, String address) {
        for(StorableStaticInstance s : storableStaticInstances) {
            if(s.id.equalsIgnoreCase(id)) {
                return false;
            }
        }
        storableStaticInstances.add(new StorableStaticInstance(id, address));
        DiscordManager.getInstance().send(new StaticInstanceAddedEmbed(service, id));
        return true;
    }

    /**
     * Remove a static instance
     * @param service The service that removed the instance
     * @param id ID of the instance
     * @return True if successfully removed, false if not
     */
    public boolean removeStaticInstance(ITerraBungeeService service, String id) {
        boolean removed = storableStaticInstances.removeIf(s -> s.id.equalsIgnoreCase(id));
        if(removed) {
            DiscordManager.getInstance().send(new StaticInstanceRemovedEmbed(service, id));
        }
        return removed;
    }

    /**
     * Gets all instances
     * @return All instances
     */
    public List<Instance> getInstances() {
        return getInstances(false);
    }

    /**
     * Gets all instances
     * @param discarded Determines whether the result should include discarded instances
     * @return All instances
     */
    public List<Instance> getInstances(boolean discarded) {
        List<Instance> instances = new ArrayList<>();
        for(TerraBungeeService s : ServiceManager.getInstance().getServices(ServiceType.INSTANCE))
            if(discarded || s.getStatus() != ServiceStatus.DISCARDED) instances.add((Instance) s);

        return instances;
    }

    /**
     * Updates the master list of services from instances
     */
    private void updateInstances() {
        List<TerraBungeeService> currentInstances = ServiceManager.getInstance().getServices(ServiceType.INSTANCE);

        // STATIC INSTANCES
        // TODO: Redo instance object and change this
        List<Instance> removeStaticInstances = Lists.newArrayList();
        for(TerraBungeeService s : currentInstances) {
            Instance instance = (Instance) s;
            if(instance.getInstanceType() == Instance.InstanceType.STATIC) {
                removeStaticInstances.add(instance);
            }
        }
        for(Instance i : removeStaticInstances) {
            ServiceManager.getInstance().discardService(i, true);
        }
        for(StorableStaticInstance s : storableStaticInstances) {
            ServiceManager.getInstance().createService(new Instance(s.id, s.address, true, true, "", ServiceStatus.ONLINE.name()
                    , Instance.InstanceType.STATIC), true);
        }
    }
}
