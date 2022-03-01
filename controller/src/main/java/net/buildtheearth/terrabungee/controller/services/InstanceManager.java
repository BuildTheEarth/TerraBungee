package net.buildtheearth.terrabungee.controller.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.noahhusby.lib.data.storage.StorageTreeMap;
import com.noahhusby.lib.data.storage.events.EventListener;
import com.noahhusby.lib.data.storage.events.transfer.StorageLoadEvent;
import lombok.Getter;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.common.services.Service;
import net.buildtheearth.terrabungee.common.services.ServiceStatus;
import net.buildtheearth.terrabungee.common.services.ServiceType;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.StaticInstanceAddedEmbed;
import net.buildtheearth.terrabungee.controller.discord.embeds.StaticInstanceRemovedEmbed;
import net.buildtheearth.terrabungee.controller.modules.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstanceManager extends Module {
    @Getter
    private static final InstanceManager instance = new InstanceManager();

    @Getter
    private final StorageTreeMap<String, StorableStaticInstance> staticInstances = new StorageTreeMap<>(StorableStaticInstance.class, String.CASE_INSENSITIVE_ORDER);

    private InstanceManager() {
        super("Instance");
    }

    /**
     * Add a static instance
     *
     * @param service The service that added the instance
     * @param id      ID of the instance
     * @param address Address of the instance
     * @return True if successfully added, false if not
     */
    public boolean addStaticInstance(Service service, String id, String address) {
        if (staticInstances.containsKey(id)) {
            return false;
        }
        staticInstances.put(id, new StorableStaticInstance(id, address));
        updateInstances();
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
    public boolean removeStaticInstance(Service service, String id) {
        boolean removed = (staticInstances.remove(id) != null);
        if (removed) {
            DiscordManager.getInstance().send(new StaticInstanceRemovedEmbed(service, id));
            updateInstances();
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
        for (Service s : ServiceManager.getInstance().getServices(ServiceType.INSTANCE)) {
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
        Map<String, StorableStaticInstance> temp = Maps.newHashMap(staticInstances);
        List<Service> currentInstances = ServiceManager.getInstance().getServices(ServiceType.INSTANCE);
        List<Instance> removalInstances = Lists.newArrayList();
        for (Service s : currentInstances) {
            Instance i = (Instance) s;
            if (!temp.containsKey(s.getId())) {
                removalInstances.add(i);
            } else {
                if (!i.getAddress().equalsIgnoreCase(temp.get(i.getId()).address)) {
                    removalInstances.add(i);
                } else {
                    temp.remove(i.getId());
                }
            }
        }
        for (Map.Entry<String, StorableStaticInstance> e : temp.entrySet()) {
            ServiceManager.getInstance().createService(new Instance(e.getKey(), e.getValue().address, true, true, "", ServiceStatus.ONLINE.name()
                    , Instance.InstanceType.STATIC), true);
        }
        for (Instance i : removalInstances) {
            ServiceManager.getInstance().discardService(i, true);
        }
    }

    @Override
    public void onEnable() {
        staticInstances.events().register(new EventListener<StorableStaticInstance>() {
            @Override
            public void onLoad(StorageLoadEvent<StorableStaticInstance> event) {
                updateInstances();
            }
        });
    }

    @Override
    public void onDisable() {

    }
}
