package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.controller.console.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

public class S2CAddStaticInstancePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.addStaticInstanceID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        for (Instance i : InstanceManager.getInstance().getInstances()) {
            if (i.getId().equalsIgnoreCase(data.get("id").getAsString())) {
                response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
                return;
            }
        }

        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
        TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, servicePacket.getID() + " created new static instance ",
                ConsoleColor.BLUE, data.get("id").getAsString(), ConsoleColor.GREEN, " with address ",
                ConsoleColor.BLUE, data.get("address").getAsString());
        InstanceManager.getInstance().addStaticInstance(ServiceManager.getInstance().getService(servicePacket.getID()), data.get("id").getAsString(), data.get("address").getAsString());
    }
}
