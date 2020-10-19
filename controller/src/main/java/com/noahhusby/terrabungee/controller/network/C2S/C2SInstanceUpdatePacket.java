package com.noahhusby.terrabungee.controller.network.C2S;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import org.json.simple.JSONObject;

public class C2SInstanceUpdatePacket implements IC2SPacket {

    private final ITerraBungeeService service;

    public C2SInstanceUpdatePacket(ITerraBungeeService service) {
        this.service = service;
    }

    @Override
    public String getID() {
        return Constants.keepAliveID;
    }

    @Override
    public JSONObject getMessage(JSONObject data) {
        return data;
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
