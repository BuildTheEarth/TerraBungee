package com.noahhusby.terrabungee.controller.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.services.ServiceStatus;
import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import com.noahhusby.terrabungee.controller.discord.embeds.ServiceDiscardedEmbed;
import com.noahhusby.terrabungee.controller.network.C2S.C2SResponsePacket;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.NetworkManager;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import org.json.simple.JSONObject;

public class S2CSetServiceStatusPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.setServiceStatusID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JSONObject data, Response response) {
        String serviceID = (String) data.get("id");
        response.responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.ERROR;
        if(ServiceManager.getInstance().getService(serviceID) == null) {
            NetworkManager.getInstance().send(new C2SResponsePacket(response));
            return;
        }

        int status = Math.round((long) data.get("status"));
        for(ServiceStatus s : ServiceStatus.values()) {
            if(s.getValue() == status) {
                ServiceManager.getInstance().getService(serviceID).setStatus(s);
                if(s == ServiceStatus.DISCARDED) DiscordManager.getInstance().send(new ServiceDiscardedEmbed(ServiceManager.getInstance().getService(serviceID)));
                response.responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.SUCCESS;
            }
        }

        NetworkManager.getInstance().send(new C2SResponsePacket(response));
    }
}
