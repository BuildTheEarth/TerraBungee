package com.noahhusby.terrabungee.controller.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.console.TextComponent;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.network.NetworkManager;
import com.noahhusby.terrabungee.controller.services.InstanceManager;
import org.json.simple.JSONObject;

public class S2CRemoveStaticInstancePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.removeStaticInstanceID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JSONObject data, Response response) {
        for(Instance i : InstanceManager.getInstance().getInstances()) {
            if(i.getId().equalsIgnoreCase((String) data.get("id"))) {
                InstanceManager.getInstance().removeStaticInstance((String) data.get("id"));
                TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.GREEN, servicePacket.getID() + " removed static instance "),
                        new TextComponent(ConsoleColor.BLUE, (String) data.get("id")));
                response.responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.SUCCESS;
                NetworkManager.getInstance().respond(response);
                return;
            }
        }

        response.responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.ERROR;
        NetworkManager.getInstance().respond(response);
    }
}
