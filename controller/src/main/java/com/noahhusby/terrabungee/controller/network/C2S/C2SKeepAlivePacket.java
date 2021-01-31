package com.noahhusby.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.TerraBungeeService;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.players.PlayerManager;
import com.noahhusby.terrabungee.controller.services.InstanceManager;
import com.noahhusby.terrabungee.controller.services.ServiceManager;

import javax.swing.*;

public class C2SKeepAlivePacket implements IC2SPacket {

    private final TerraBungeeService service;

    public C2SKeepAlivePacket(TerraBungeeService service) {
        this.service = service;
    }

    @Override
    public String getID() {
        return Constants.keepAliveID;
    }

    @Override
    public void getMessage(JsonObject data) {
        data.addProperty("version", com.noahhusby.terrabungee.controller.Constants.version);
        data.addProperty("total_services", ServiceManager.getInstance().getServices().size());
        data.addProperty("total_disconnected_services", ServiceManager.getInstance().getTotalDisconnectedServices());
        data.addProperty("total_players", PlayerManager.getInstance().getTotalPlayers());
        data.addProperty("total_online_players", PlayerManager.getInstance().getTotalOnlinePlayers());
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
