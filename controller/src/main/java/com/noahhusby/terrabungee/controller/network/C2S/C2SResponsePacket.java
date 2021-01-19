package com.noahhusby.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;

public class C2SResponsePacket implements IC2SPacket {
    private final Response response;

    public C2SResponsePacket(Response response) {
        this.response = response;
    }

    @Override
    public String getID() {
        return Constants.responseID;
    }

    @Override
    public void getMessage(JsonObject data) {
        data.addProperty("response_code", response.getCode().name());
        data.addProperty("salt", response.getSalt());
        data.add("response_data", response.getData());
    }

    @Override
    public ServicePacket getServicePacket() {
        return response.getServicePacket();
    }
}
