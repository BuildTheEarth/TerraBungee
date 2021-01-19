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
        return Constants.packetResponseID;
    }

    @Override
    public void getMessage(JsonObject data) {
        data.addProperty("salt", response.salt);
        data.addProperty("response_code", response.responseCode.name());
        //TODO: Add Json Responses
        data.addProperty("response", new JsonObject().getAsString());
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(response.service);
    }
}
