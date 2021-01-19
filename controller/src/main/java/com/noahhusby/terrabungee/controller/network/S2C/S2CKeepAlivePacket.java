package com.noahhusby.terrabungee.controller.network.S2C;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.api.services.ServiceType;
import com.noahhusby.terrabungee.controller.network.C2S.C2SKeepAlivePacket;
import com.noahhusby.terrabungee.controller.network.C2S.C2SResponsePacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.network.NetworkManager;

import java.util.List;

public class S2CKeepAlivePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.keepAliveID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        ServiceType type = ServiceType.valueOf(data.get("type").getAsString());
        List<ServiceIntent> intents = TerraBungeeUtil.arrayToIntents(TerraBungeeUtil.parse(data.get("intents").getAsString()).getAsJsonArray());
        ServiceManager.getInstance().initService(type, servicePacket.getID(), servicePacket.getClient(), intents);
        ServiceManager.getInstance().getService(servicePacket.getID()).keepAlive();

        response.responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.SUCCESS;
        if(response.salt != null) NetworkManager.getInstance().send(new C2SResponsePacket(response));

        NetworkManager.getInstance().send(new C2SKeepAlivePacket(ServiceManager.getInstance().getService(servicePacket.getID())));
    }
}
