package com.noahhusby.terrabungee.controller.services;

import com.google.gson.annotations.Expose;
import com.noahhusby.terrabungee.api.services.*;
import com.noahhusby.terrabungee.controller.config.ConfigHandler;
import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import com.noahhusby.terrabungee.controller.discord.embeds.StaticInstanceAddedEmbed;
import com.noahhusby.terrabungee.controller.discord.embeds.StaticInstanceRemovedEmbed;

import java.util.ArrayList;
import java.util.List;

public class InstanceManager {
    private static InstanceManager instance;

    public static InstanceManager getInstance() {
        return instance;
    }

    public static void setInstance(InstanceManager instance) {
        InstanceManager.instance = instance;
        instance.updateInstances();
    }

    public InstanceManager() {
        updateInstances();
    }

    @Expose
    public List<StorableStaticInstance> storableStaticInstances = new ArrayList<>();

    public boolean addStaticInstance(ITerraBungeeService service, String id, String address) {
        for(StorableStaticInstance s : storableStaticInstances)
            if(s.id.equalsIgnoreCase(id)) return false;

        DiscordManager.getInstance().send(new StaticInstanceAddedEmbed(service, id));
        storableStaticInstances.add(new StorableStaticInstance(id, address));

        updateInstances();
        ConfigHandler.getInstance().saveStaticInstances();
        return true;
    }

    public boolean removeStaticInstance(ITerraBungeeService service, String id) {
        boolean removed = false;
        List<StorableStaticInstance> temp = new ArrayList<>();
        for(StorableStaticInstance s : storableStaticInstances)
            if(s.id.equalsIgnoreCase(id)) {
                removed = true;
                temp.add(s);
            }


        for(StorableStaticInstance s : temp)
            storableStaticInstances.remove(s);

        if(removed)
            DiscordManager.getInstance().send(new StaticInstanceRemovedEmbed(service, id));

        updateInstances();
        ConfigHandler.getInstance().saveStaticInstances();
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

    public void reload() {
        ConfigHandler.getInstance().loadStaticInstances();
    }

    private void updateInstances() {
        List<TerraBungeeService> currentInstances = ServiceManager.getInstance().getServices(ServiceType.INSTANCE);
        List<Instance> removeStaticInstances = new ArrayList<>();
        for(TerraBungeeService s : currentInstances)
            if(((Instance) s).getInstanceType() == Instance.InstanceType.STATIC) removeStaticInstances.add((Instance) s);

        for(Instance i : removeStaticInstances)
            ServiceManager.getInstance().discardService(i, true);

        for(StorableStaticInstance s : storableStaticInstances) {
            ServiceManager.getInstance().createService(new Instance(s.id, s.address, true, true, "", ServiceStatus.ONLINE.name()
                    , Instance.InstanceType.STATIC), true);
        }
    }

}
