package net.buildtheearth.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;

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
