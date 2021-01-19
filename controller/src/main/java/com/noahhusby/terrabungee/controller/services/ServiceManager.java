package com.noahhusby.terrabungee.controller.services;

import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.services.*;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.List;

public class ServiceManager {
    private static ServiceManager instance;

    public static ServiceManager getInstance() {
        if(instance == null) instance = new ServiceManager();
        return instance;
    }

    private List<TerraBungeeService> services = new ArrayList<>();
    private String defaultServer = "";

    private ServiceManager() { }

    /**
     * Gets all services created regardless of state (unless discarded)
     * @return All services created
     */
    public List<TerraBungeeService> getServices() {
        return services;
    }

    public int getTotalDisconnectedServices() {
        int x = 0;
        for(TerraBungeeService s : services)
            if(s.getStatus() == ServiceStatus.LOST_CONNECTION) x++;

        return x;
    }

    /**
     * Gets all servers created of a certain type regardless of state (unless discarded)
     * @param type The type of service
     * @return All services of the same type
     */
    public List<TerraBungeeService> getServices(ServiceType type) {
        List<TerraBungeeService> typeServices = new ArrayList<>();
        for(TerraBungeeService s : services)
            if(s.getType() == type) typeServices.add(s);

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
     * @param id Service ID
     * @return TerraBungeeService
     */
    public TerraBungeeService getService(String id) {
        for(TerraBungeeService s : services)
            if(s.getId().equalsIgnoreCase(id)) return s;
        return null;
    }

    /**
     * Creates a new service from a service initialization packet
     * @param type Type of service
     * @param ID The ID of the service
     * @param client The websocket client
     * @param intents The intents
     */
    public void initService(ServiceType type, String ID, WsContext client, List<ServiceIntent> intents) {
        if(getService(ID) != null) {
            getService(ID).setIntents(intents);
            getService(ID).setClient(client);
            getService(ID).setStatus(ServiceStatus.ONLINE);
            return;
        }

        TerraBungeeService service = createService(type, ID);

        if(service == null) {
            //TODO: Track if this service should've been awaiting initialization but somehow wasn't.
            return;
        }

        service.setStatus(ServiceStatus.ONLINE);
        service.setClient(client);
        service.setIntents(intents);

        TerraBungeeController.logger.info("Initialized new service (" + type.name() + "): " + ID);
    }

    /**
     * Sets a service's state to DISCARDED
     * @param service The service to discard
     */
    public void discardService(TerraBungeeService service) {
        discardService(service, false);
    }

    /**
     * Sets a service's state to DISCARDED
     * @param service The service to discard
     * @param remove Whether the service should be discarded or removed completely
     */
    public void discardService(TerraBungeeService service, boolean remove) {
        if(remove) {
            services.remove(service);
            return;
        }

        getService(service.getId()).setStatus(ServiceStatus.DISCARDED);
    }

    /**
     * Creates a new service from a service type and ID. Useful for new services from initializations (Ex: Proxy)
     * @param type The type of service
     * @param ID The ID of the service
     * @return The new service
     */
    public TerraBungeeService createService(ServiceType type, String ID) {
        if(type == ServiceType.PROXY) {
            return createService(new Proxy(ID));
        } else if(type == ServiceType.CUSTOM) {
            return createService(new Custom(ID));
        }

        return null;
    }

    /**
     * Creates a service that the controller expects to be initalized
     * @param service The service that should be initialized in the future
     * @return The new service
     */
    public TerraBungeeService createService(TerraBungeeService service) {
        return createService(service, false);
    }

    /**
     * Creates a service that the controller expects to be initialized
     * @param service The service that should be initialized in the future
     * @param staticService If the service is static, or not. Setting this to true will assume that the service won't be initialized
     * @return The new service
     */
    public TerraBungeeService createService(TerraBungeeService service, boolean staticService) {
        if(getService(service.getId()) != null) return service;

        service.setStatus(ServiceStatus.AWAIT_INIT);
        if(staticService) service.setStatus(ServiceStatus.STATIC);

        services.add(service);
        return service;
    }
}
