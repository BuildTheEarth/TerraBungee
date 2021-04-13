package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.services.ServiceStatus;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.ServiceDiscardedEmbed;
import net.buildtheearth.terrabungee.controller.network.IS2CPacket;
import net.buildtheearth.terrabungee.controller.network.Response;
import net.buildtheearth.terrabungee.controller.network.ServicePacket;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

public class S2CSetServiceStatusPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.setServiceStatusID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        String serviceID = data.get("id").getAsString();
        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
        if (ServiceManager.getInstance().getService(serviceID) == null) {
            return;
        }

        int status = data.get("status").getAsInt();
        for (ServiceStatus s : ServiceStatus.values()) {
            if (s.getValue() == status) {
                ServiceManager.getInstance().getService(serviceID).setStatus(s);
                if (s == ServiceStatus.DISCARDED) {
                    DiscordManager.getInstance().send(new ServiceDiscardedEmbed(ServiceManager.getInstance().getService(serviceID)));
                }
                response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
            }
        }
    }
}
