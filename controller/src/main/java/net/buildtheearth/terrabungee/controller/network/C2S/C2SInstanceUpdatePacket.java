package net.buildtheearth.terrabungee.controller.network.C2S;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;

public class C2SInstanceUpdatePacket implements IC2SPacket {

    private final TerraBungeeService service;

    public C2SInstanceUpdatePacket(TerraBungeeService service) {
        this.service = service;
    }

    @Override
    public String getID() {
        return Constants.instanceUpdateID;
    }

    @Override
    public void getMessage(JsonObject data) {
        JsonArray instanceArray = new JsonArray();
        for (Instance i : InstanceManager.getInstance().getInstances()) {
            instanceArray.add(TerraBungeeUtil.GSON.toJsonTree(i));
        }

        data.add("instances", instanceArray);
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(service);
    }
}
