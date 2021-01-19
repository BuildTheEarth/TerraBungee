package com.noahhusby.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.network.NetworkManager;

public class S2CResponsePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.packetResponseID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        NetworkManager.getInstance().onIncomingPayload(servicePacket.getClient(), data.get("packet").getAsString(),
                data.get("salt").getAsString());
    }
}
