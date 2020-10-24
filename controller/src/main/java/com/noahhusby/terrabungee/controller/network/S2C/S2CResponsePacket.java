package com.noahhusby.terrabungee.controller.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.network.TerraBungeeNetworkManager;
import org.json.simple.JSONObject;

public class S2CResponsePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.packetResponseID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JSONObject data, Response response) {
        TerraBungeeNetworkManager.getInstance().onIncomingPayload(servicePacket.getClient(),
                (String) data.get("packet"), (String) data.get("salt"));
    }
}
