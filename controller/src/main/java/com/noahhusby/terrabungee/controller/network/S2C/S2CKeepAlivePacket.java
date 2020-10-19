package com.noahhusby.terrabungee.controller.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.TerraBungeeService;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import com.noahhusby.terrabungee.controller.network.C2S.C2SInstanceUpdatePacket;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.network.TerraBungeeNetworkManager;
import org.json.simple.JSONObject;

public class S2CKeepAlivePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.keepAliveID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JSONObject data) {
        TerraBungeeService service = ServiceManager.getInstance().getService(servicePacket.getID());
        if(service == null) return;
        service.keepAlive();
        TerraBungeeNetworkManager.getInstance().sendPayload(new C2SInstanceUpdatePacket(service));
    }
}
