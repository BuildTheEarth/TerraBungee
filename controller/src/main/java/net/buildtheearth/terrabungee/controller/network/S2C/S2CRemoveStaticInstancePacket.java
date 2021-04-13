package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.services.Instance;
import net.buildtheearth.terrabungee.controller.console.ConsoleColor;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.network.IS2CPacket;
import net.buildtheearth.terrabungee.controller.network.Response;
import net.buildtheearth.terrabungee.controller.network.ServicePacket;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

public class S2CRemoveStaticInstancePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.removeStaticInstanceID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        for (Instance i : InstanceManager.getInstance().getInstances()) {
            if (i.getId().equalsIgnoreCase(data.get("id").getAsString())) {
                InstanceManager.getInstance().removeStaticInstance(ServiceManager.getInstance().getService(servicePacket.getID()), data.get("id").getAsString());
                TerraBungeeConsole.sendMessage(ConsoleColor.GREEN, servicePacket.getID() + " removed static instance ",
                        ConsoleColor.BLUE, data.get("id").getAsString());
                response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
                return;
            }
        }

        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
    }
}
