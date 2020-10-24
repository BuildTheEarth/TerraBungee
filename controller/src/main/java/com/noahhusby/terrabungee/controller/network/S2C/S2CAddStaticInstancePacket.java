package com.noahhusby.terrabungee.controller.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.network.TerraBungeeNetworkManager;
import com.noahhusby.terrabungee.controller.services.InstanceManager;
import org.json.simple.JSONObject;

public class S2CAddStaticInstancePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.addStaticInstanceID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JSONObject data, Response response) {
        for(Instance i : InstanceManager.getInstance().getInstances()) {
            if(i.getId().equalsIgnoreCase((String) data.get("id"))) {
                response.responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.ERROR;
                TerraBungeeNetworkManager.getInstance().respond(response);
                return;
            }
        }


    }
}
