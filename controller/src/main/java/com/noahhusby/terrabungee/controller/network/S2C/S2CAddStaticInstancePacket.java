package com.noahhusby.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
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
import com.noahhusby.terrabungee.controller.services.ServiceManager;

public class S2CAddStaticInstancePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.addStaticInstanceID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        for(Instance i : InstanceManager.getInstance().getInstances()) {
            if(i.getId().equalsIgnoreCase(data.get("id").getAsString())) {
                response.responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.ERROR;
                NetworkManager.getInstance().respond(response);
                return;
            }
        }

        response.responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.SUCCESS;
        TerraBungeeConsole.sendMessage(new TextComponent(ConsoleColor.GREEN, servicePacket.getID() + " created new static instance "),
                new TextComponent(ConsoleColor.BLUE, data.get("id").getAsString()), new TextComponent(ConsoleColor.GREEN, " with address "),
                new TextComponent(ConsoleColor.BLUE, data.get("address").getAsString()));
        InstanceManager.getInstance().addStaticInstance(ServiceManager.getInstance().getService(servicePacket.getID()), data.get("id").getAsString(), data.get("address").getAsString());
        NetworkManager.getInstance().respond(response);
    }
}
