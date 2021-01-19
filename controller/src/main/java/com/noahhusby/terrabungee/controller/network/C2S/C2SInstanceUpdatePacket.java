package com.noahhusby.terrabungee.controller.network.C2S;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.noahhusby.lib.data.JsonUtils;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.services.InstanceManager;

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
    public void getMessage(JsonObject data) {
        JsonArray instanceArray = new JsonArray();
        for(Instance i : InstanceManager.getInstance().getInstances())
            instanceArray.add(TerraBungeeController.GSON.toJson(i));

        data.add("instances", instanceArray);
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
