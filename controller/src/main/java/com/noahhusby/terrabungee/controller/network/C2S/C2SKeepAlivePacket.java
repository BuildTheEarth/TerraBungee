package com.noahhusby.terrabungee.controller.network.C2S;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.TerraBungeeService;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.services.InstanceManager;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import org.json.simple.JSONObject;

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
    public JSONObject getMessage(JSONObject data) {
        data.put("version", com.noahhusby.terrabungee.controller.Constants.version);
        data.put("total_services", ServiceManager.getInstance().getServices().size());
        data.put("total_disconnected_services", ServiceManager.getInstance().getTotalDisconnectedServices());
        return data;
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
