package com.noahhusby.terrabungee.controller.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.controller.network.C2S.C2SServiceMessagePacket;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.NetworkManager;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class S2CServiceMessagePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.serviceMessageID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JSONObject data, Response response) {
        String to = (String) data.get("to");
        if(ServiceManager.getInstance().getService(to) == null) return;
        NetworkManager.getInstance().send(new C2SServiceMessagePacket(servicePacket.getID(), to, (String) data.get("message")));
    }
}
