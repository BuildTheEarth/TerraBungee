package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SServiceMessagePacket;
import net.buildtheearth.terrabungee.controller.network.IS2CPacket;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.network.Response;
import net.buildtheearth.terrabungee.controller.network.ServicePacket;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

public class S2CServiceMessagePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.serviceMessageID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        String to = data.get("to").getAsString();
        if (ServiceManager.getInstance().getService(to) == null) {
            return;
        }
        NetworkManager.getInstance().send(new C2SServiceMessagePacket(servicePacket.getID(), to, data.get("message").getAsString()));
    }
}
