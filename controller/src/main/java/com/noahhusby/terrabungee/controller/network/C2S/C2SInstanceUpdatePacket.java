package com.noahhusby.terrabungee.controller.network.C2S;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.services.InstanceManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class C2SInstanceUpdatePacket implements IC2SPacket {

    private final ITerraBungeeService service;

    public C2SInstanceUpdatePacket(ITerraBungeeService service) {
        this.service = service;
    }

    @Override
    public String getID() {
        return Constants.instanceUpdateID;
    }

    @Override
    public JSONObject getMessage(JSONObject data) {
        JSONArray instanceArray = new JSONArray();
        for(Instance i : InstanceManager.getInstance().getInstances()) {
            instanceArray.add(i.toJSON());
        }

        data.put("instances", instanceArray);
        return data;
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
