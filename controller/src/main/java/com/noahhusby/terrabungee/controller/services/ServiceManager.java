package com.noahhusby.terrabungee.controller.services;

import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.api.services.Proxy;
import com.noahhusby.terrabungee.api.services.TerraBungeeService;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import io.javalin.websocket.WsContext;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceManager {
    private static ServiceManager instance;

    public static ServiceManager getInstance() {
        if(instance == null) instance = new ServiceManager();
        return instance;
    }

    private List<TerraBungeeService> registeredServices = new ArrayList<>();

    private ServiceManager() { }

    public List<TerraBungeeService> getServices() {
        return registeredServices;
    }

    /**
     * Gets a registered service by it's ID
     * @param id Service ID
     * @return TerraBungeeService
     */
    public TerraBungeeService getService(String id) {
        for(TerraBungeeService s : registeredServices)
            if(s.getId().equalsIgnoreCase(id)) return s;
        return null;
    }

    /**
     * Adds/updates bungeecord proxy
     * @param client The websocket client
     * @param ID Terrabungee's service ID
     */
    public void initProxy(WsContext client, String ID, List<ServiceIntent> intents) {
        if(getService(ID) != null) {
            getService(ID).setIntents(intents);
            return;
        }

        Proxy proxy = new Proxy(ID);
        proxy.setClient(client);
        proxy.setIntents(intents);
        registeredServices.add(proxy);
        TerraBungeeController.logger.info("Registered new proxy: " + ID);
        //DiscordManager.getInstance().send(new ProxyAddedEmbed(proxy));
    }
}
