package net.buildtheearth.terrabungee.controller.instance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.noahhusby.lib.data.storage.StorageTreeMap;
import com.noahhusby.lib.data.storage.events.EventListener;
import com.noahhusby.lib.data.storage.events.transfer.StorageLoadEvent;
import lombok.Getter;
import net.buildtheearth.terrabungee.common.services.Node;
import net.buildtheearth.terrabungee.common.services.Service;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.StaticInstanceAddedEmbed;
import net.buildtheearth.terrabungee.controller.discord.embeds.StaticInstanceRemovedEmbed;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.buildtheearth.terrabungee.controller.services.ServiceController;
import net.buildtheearth.terrabungee.common.instance.Instance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstanceManager extends Module {
    @Getter
    private static final InstanceManager instance = new InstanceManager();

    @Getter
    private final StorageTreeMap<String, StorableStaticInstance> staticInstances = new StorageTreeMap<>(StorableStaticInstance.class, String.CASE_INSENSITIVE_ORDER);

    @Getter
    private final ConcurrentHashMap<String, Instance> instances = new ConcurrentHashMap<>();

    private InstanceManager() {
        super("instance");
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
     * Updates the master list of services from instances
     */
    private void updateInstances() {
        Map<String, StorableStaticInstance> temp = Maps.newHashMap(staticInstances);
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

    @Override
    public List<String> getRequiredModules() {
        return Lists.newArrayList("storage");
    }

    private class NodeServiceController extends ServiceController<Node> {

        @Override
        public void onServiceConnect(Node service) {
        }

        @Override
        public void onServiceInit(Node service) {

        }
    }
}
