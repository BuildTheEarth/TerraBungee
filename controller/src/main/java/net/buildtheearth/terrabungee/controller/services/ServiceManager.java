package net.buildtheearth.terrabungee.controller.services;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.TerraBungeeVersion;
import net.buildtheearth.terrabungee.common.services.Custom;
import net.buildtheearth.terrabungee.common.services.Proxy;
import net.buildtheearth.terrabungee.common.services.Service;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;
import net.buildtheearth.terrabungee.common.services.ServiceStatus;
import net.buildtheearth.terrabungee.common.services.ServiceType;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.ServiceOfflineEmbed;
import net.buildtheearth.terrabungee.controller.discord.embeds.ServiceReconnectedEmbed;
import net.buildtheearth.terrabungee.controller.exceptions.ServiceControllerRegisteredException;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SInstanceUpdatePacket;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SOnlinePlayerCacheHitPacket;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import org.java_websocket.WebSocket;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ServiceManager extends Module {
    private static ServiceManager instance = null;

    public static ServiceManager getInstance() {
        return instance == null ? instance = new ServiceManager() : instance;
    }

    private final Map<String, Service> services = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
    private String defaultServer = "";

    private final ScheduledExecutorService intentThreads = TerraBungeeUtil.newThreadPoolScheduledExecutor(32, "terrabungee-intents");

    private final Map<Class<? extends Service>, ServiceController<? extends Service>> serviceControllers = new ConcurrentHashMap<>();

    /**
     * Registers a service controller to handle a specific service type.
     *
     * @param serviceController {@link ServiceController}.
     * @param clazz The class object of the registered service.
     * @throws ServiceControllerRegisteredException if there is a controller already registered for the specified service.
     */
    public void registerController(ServiceController<? extends Service> serviceController, Class<? extends Service> clazz) throws ServiceControllerRegisteredException {
        if(serviceControllers.containsKey(clazz)) {
            throw new ServiceControllerRegisteredException(clazz);
        }
        serviceControllers.put(clazz, serviceController);
    }

    /**
     * Gets all services created regardless of state (unless discarded)
     *
     * @return All services created
     */
    public Map<String, Service> getServices() {
        return ImmutableMap.copyOf(services);
    }

    private ServiceManager() {
        super("services");
    }

    public int getTotalDisconnectedServices() {
        int x = 0;
        for (Service s : ImmutableMap.copyOf(services).values()) {
            if (s.getStatus() == ServiceStatus.LOST_CONNECTION) {
                x++;
            }
        }
        return x;
    }

    /**
     * Gets all servers created of a certain type regardless of state (unless discarded)
     *
     * @param type The type of service
     * @return All services of the same type
     */
    public List<Service> getServices(ServiceType type) {
        List<Service> typeServices = new ArrayList<>();
        for (Service s : ImmutableMap.copyOf(services).values()) {
            if (s.getType() == type) {
                typeServices.add(s);
            }
        }
        return typeServices;
    }

    public String getDefaultServer() {
        return defaultServer;
    }

    public void setDefaultServer(String id) {
        this.defaultServer = id;
    }

    /**
     * Gets a registered service by it's ID
     *
     * @param id Service ID
     * @return TerraBungeeService
     */
    public Service getService(String id) {
        return services.get(id);
    }

    /**
     * Creates a new service from a service initialization packet
     *
     * @param type    Type of service
     * @param ID      The ID of the service
     * @param client  The websocket client
     * @param intents The intents
     */
    public Service initService(ServiceType type, String ID, TerraBungeeVersion version, WebSocket client, List<ServiceIntent> intents) {
        Service service = getService(ID);
        if (service != null) {
            service.setIntents(intents);
            service.setVersion(version);
            service.setClient(client);
            service.setStatus(ServiceStatus.ONLINE);
            //TODO: Remove this manual caching method
            if (getService(ID) instanceof Proxy) {
                PlayerManager.getInstance().pushMuteCache(getService(ID));
            }
            return getService(ID);
        }

        service = createService(type, ID);

        if (service == null) {
            //TODO: Track if this service should've been awaiting initialization but somehow wasn't.
            return null;
        }

        service.setVersion(version);
        service.setStatus(ServiceStatus.ONLINE);
        service.setClient(client);
        service.setIntents(intents);

        TerraBungeeController.logger.info("Initialized new service (" + type.name() + "): " + ID);

        //TODO: Remove this manual caching method
        if (service instanceof Proxy) {
            PlayerManager.getInstance().pushMuteCache(service);
        }
        return service;
    }

    /**
     * Sets a service's state to DISCARDED
     *
     * @param service The service to discard
     */
    public void discardService(Service service) {
        discardService(service, false);
    }

    /**
     * Sets a service's state to DISCARDED
     *
     * @param service The service to discard
     * @param remove  Whether the service should be discarded or removed completely
     */
    public void discardService(Service service, boolean remove) {
        if (remove) {
            services.remove(service.getId());
            return;
        }

        getService(service.getId()).setStatus(ServiceStatus.DISCARDED);
    }

    /**
     * Creates a new service from a service type and ID. Useful for new services from initializations (Ex: Proxy)
     *
     * @param type The type of service
     * @param ID   The ID of the service
     * @return The new service
     */
    public Service createService(ServiceType type, String ID) {
        if (type == ServiceType.PROXY) {
            return createService(new Proxy(ID));
        } else if (type == ServiceType.CUSTOM) {
            return createService(new Custom(ID));
        }

        return null;
    }

    /**
     * Creates a service that the controller expects to be initalized
     *
     * @param service The service that should be initialized in the future
     * @return The new service
     */
    public Service createService(Service service) {
        return createService(service, false);
    }

    /**
     * Creates a service that the controller expects to be initialized
     *
     * @param service       The service that should be initialized in the future
     * @param staticService If the service is static, or not. Setting this to true will assume that the service won't be initialized
     * @return The new service
     */
    public Service createService(Service service, boolean staticService) {
        if (getService(service.getId()) != null) {
            return service;
        }

        service.setStatus(ServiceStatus.AWAIT_INIT);
        if (staticService) {
            service.setStatus(ServiceStatus.STATIC);
        }

        services.put(service.getId(), service);
        return service;
    }

    /**
     * Assigns an action to services with a given intent on a clock
     *
     * @param intent  {@link ServiceIntent}
     * @param seconds Time (in seconds) that the intent action should be triggered
     * @param service {@link Consumer< Service >}
     */
    private void registerIntent(ServiceIntent intent, int seconds, Consumer<Service> service) {
        intentThreads.scheduleAtFixedRate(() -> runIntentAction(intent, service), 1, seconds, TimeUnit.SECONDS);
    }

    /**
     * Runs a specific action on all services with a specific intent
     *
     * @param intent  {@link ServiceIntent}
     * @param service {@link Consumer< Service >}
     */
    public void runIntentAction(ServiceIntent intent, Consumer<Service> service) {
        for (Service s : ImmutableMap.copyOf(services).values()) {
            if (s.getStatus() == ServiceStatus.ONLINE && s.getIntents().contains(intent)) {
                intentThreads.submit(() -> service.accept(s));
            }
        }
    }

    @Override
    public void onEnable() {
        registerIntent(ServiceIntent.INSTANCE_UPDATE, 2, s -> NetworkManager.getInstance().send(new C2SInstanceUpdatePacket(s)));
        registerIntent(ServiceIntent.ONLINE_PLAYER_UPDATE, 2, s -> NetworkManager.getInstance().send(new C2SOnlinePlayerCacheHitPacket(s)));
        TerraBungeeController.getInstance().getGeneralThreads().scheduleAtFixedRate(new ServiceChecker(), 1, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        intentThreads.shutdownNow();
    }

    @Override
    public List<String> getRequiredModules() {
        return Lists.newArrayList("storage");
    }

    /**
     * A runnable class that checks the connection status of each service
     */
    public static class ServiceChecker implements Runnable {

        private final Map<String, ServiceStatus> serviceStatus = new HashMap<>();

        @Override
        public void run() {
            for (Map.Entry<String, ServiceStatus> s : serviceStatus.entrySet()) {
                Service service = ServiceManager.getInstance().getService(s.getKey());
                if (service == null) {
                    continue;
                }
                if (s.getValue() == ServiceStatus.ONLINE && service.getStatus() == ServiceStatus.LOST_CONNECTION) {
                    ServiceManager.instance.getLogger().warn("Service has lost connection with the controller: " + s.getKey());
                    DiscordManager.getInstance().send(new ServiceOfflineEmbed(service));
                }
                if (s.getValue() == ServiceStatus.LOST_CONNECTION && service.getStatus() == ServiceStatus.ONLINE) {
                    ServiceManager.instance.getLogger().warn("Service has reconnected with the controller: " + s.getKey());
                    DiscordManager.getInstance().send(new ServiceReconnectedEmbed(service));
                }
            }

            serviceStatus.clear();
            for (Service s : ServiceManager.getInstance().getServices().values()) {
                serviceStatus.put(s.getId(), s.getStatus());
            }
        }
    }
}
