package com.noahhusby.terrabungee.controller.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.api.services.Proxy;
import com.noahhusby.terrabungee.api.services.ServiceType;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class S2CServiceInitPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.serviceInitID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JSONObject data) {
        ServiceManager.getInstance().initService(ServiceType.valueOf((String) data.get("type")), servicePacket.getID(),
                servicePacket.getClient(), TerraBungeeUtil.arrayToIntents((JSONArray) TerraBungeeUtil.stringToJSON((String) data.get("intents"))));
    }
}
