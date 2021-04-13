package net.buildtheearth.terrabungee.controller.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.noahhusby.lib.data.storage.StorageList;
import lombok.Getter;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.common.services.ServiceStatus;
import net.buildtheearth.terrabungee.common.services.ServiceType;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.StaticInstanceAddedEmbed;
import net.buildtheearth.terrabungee.controller.discord.embeds.StaticInstanceRemovedEmbed;

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

    @Getter
    private final StorageList<StorableStaticInstance> storableStaticInstances = new StorageList<>(StorableStaticInstance.class);

    /**
     * Add a static instance
     *
     * @param service The service that added the instance
     * @param id      ID of the instance
     * @param address Address of the instance
     * @return True if successfully added, false if not
     */
    public boolean addStaticInstance(TerraBungeeService service, String id, String address) {
        for (StorableStaticInstance s : storableStaticInstances) {
            if (s.id.equalsIgnoreCase(id)) {
                return false;
            }
        }
        storableStaticInstances.add(new StorableStaticInstance(id, address));
        DiscordManager.getInstance().send(new StaticInstanceAddedEmbed(service, id));
        return true;
    }

    /**
     * Remove a static instance
     *
     * @param service The service that removed the instance
     * @param id      ID of the instance
     * @return True if successfully removed, false if not
     */
    public boolean removeStaticInstance(TerraBungeeService service, String id) {
        boolean removed = storableStaticInstances.removeIf(s -> s.id.equalsIgnoreCase(id));
        if (removed) {
            DiscordManager.getInstance().send(new StaticInstanceRemovedEmbed(service, id));
        }
        return removed;
    }

    /**
     * Gets all instances
     *
     * @return All instances
     */
    public List<Instance> getInstances() {
        return getInstances(false);
    }

    /**
     * Gets all instances
     *
     * @param discarded Determines whether the result should include discarded instances
     * @return All instances
     */
    public List<Instance> getInstances(boolean discarded) {
        List<Instance> instances = new ArrayList<>();
        for (TerraBungeeService s : ServiceManager.getInstance().getServices(ServiceType.INSTANCE)) {
            if (discarded || s.getStatus() != ServiceStatus.DISCARDED) {
                instances.add((Instance) s);
            }
        }

        return instances;
    }

    /**
     * Updates the master list of services from instances
     */
    private void updateInstances() {
        List<TerraBungeeService> currentInstances = ServiceManager.getInstance().getServices(ServiceType.INSTANCE);
        List<Instance> removalInstances = Lists.newArrayList();

        // STATIC INSTANCES
        Map<String, String> staticInstanceMap = Maps.newHashMap();
        for (StorableStaticInstance s : storableStaticInstances) {
            staticInstanceMap.put(s.id, s.address);
        }
        for (TerraBungeeService s : currentInstances) {
            Instance instance = (Instance) s;
            if (instance.getInstanceType() == Instance.InstanceType.STATIC) {
                if (!staticInstanceMap.containsKey(instance.getId())) {
                    removalInstances.add(instance);
                } else {
                    if (!instance.getAddress().equalsIgnoreCase(staticInstanceMap.get(instance.getId()))) {
                        removalInstances.add(instance);
                    } else {
                        staticInstanceMap.remove(instance.getId());
                    }
                }
            }
        }
        for (Map.Entry<String, String> e : staticInstanceMap.entrySet()) {
            ServiceManager.getInstance().createService(new Instance(e.getKey(), e.getValue(), true, true, "", ServiceStatus.ONLINE.name()
                    , Instance.InstanceType.STATIC), true);
        }
        for (Instance i : removalInstances) {
            ServiceManager.getInstance().discardService(i, true);
        }
    }
}
