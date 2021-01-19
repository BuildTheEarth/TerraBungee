package com.noahhusby.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.ServiceStatus;
import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import com.noahhusby.terrabungee.controller.discord.embeds.ServiceDiscardedEmbed;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.services.ServiceManager;

public class S2CSetServiceStatusPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.setServiceStatusID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        String serviceID = data.get("id").getAsString();
        response.setCode(com.noahhusby.terrabungee.api.network.Response.ResponseCode.ERROR);
        if(ServiceManager.getInstance().getService(serviceID) == null) return;

        int status = data.get("status").getAsInt();
        for(ServiceStatus s : ServiceStatus.values()) {
            if(s.getValue() == status) {
                ServiceManager.getInstance().getService(serviceID).setStatus(s);
                if(s == ServiceStatus.DISCARDED) DiscordManager.getInstance().send(new ServiceDiscardedEmbed(ServiceManager.getInstance().getService(serviceID)));
                response.setCode(com.noahhusby.terrabungee.api.network.Response.ResponseCode.SUCCESS);
            }
        }
    }
}
