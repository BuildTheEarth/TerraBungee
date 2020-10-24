package com.noahhusby.terrabungee.controller.network.C2S;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.controller.network.IC2SPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import org.json.simple.JSONObject;

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
    public JSONObject getMessage(JSONObject data) {
        data.put("salt", response.salt);
        data.put("response_code", response.responseCode.name());
        data.put("response", response.responseData.toJSONString());
        return data;
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(response.service);
    }
}
