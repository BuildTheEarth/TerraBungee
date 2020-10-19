package com.noahhusby.terrabungee.controller.services;

import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.api.services.ServiceStatus;
import com.noahhusby.terrabungee.api.services.TerraBungeeService;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import com.noahhusby.terrabungee.controller.discord.embeds.ServiceOfflineEmbed;
import com.noahhusby.terrabungee.controller.discord.embeds.ServiceReconnectedEmbed;

import java.util.HashMap;
import java.util.Map;

public class ServiceChecker implements Runnable {
    Map<String, ServiceStatus> serviceStatus = new HashMap<>();
    @Override
    public void run() {
        for(Map.Entry<String, ServiceStatus> s : serviceStatus.entrySet()) {
            TerraBungeeService service = ServiceManager.getInstance().getService(s.getKey());
            if(service == null) continue;
            if(s.getValue() == ServiceStatus.ONLINE && service.getStatus() == ServiceStatus.LOST_CONNECTION) {
                TerraBungeeController.logger.warning("Service has lost connection with the controller: " + s.getKey());
                DiscordManager.getInstance().send(new ServiceOfflineEmbed(service));
            }
            if(s.getValue() == ServiceStatus.LOST_CONNECTION && service.getStatus() == ServiceStatus.ONLINE) {
                TerraBungeeController.logger.warning("Service has reconnected with the controller: " + s.getKey());
                for(ServiceIntent d : service.getIntents())
                    System.out.println(d.name());
                DiscordManager.getInstance().send(new ServiceReconnectedEmbed(service));
            }
        }

        serviceStatus.clear();
        for(TerraBungeeService s : ServiceManager.getInstance().getServices()) {
            serviceStatus.put(s.getId(), s.getStatus());
        }
    }
}