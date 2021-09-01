package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.TerraBungeeVersion;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;
import net.buildtheearth.terrabungee.common.services.ServiceType;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SKeepAlivePacket;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

import java.util.List;

public class S2CKeepAlivePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.keepAliveID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        ServiceType type = ServiceType.valueOf(data.get("type").getAsString());
        List<ServiceIntent> intents = TerraBungeeUtil.arrayToIntents(data.get("intents").getAsJsonArray());
        TerraBungeeVersion version = TerraBungeeUtil.GSON.fromJson(data.get("version"), TerraBungeeVersion.class);
        ServiceManager.getInstance().initService(type, servicePacket.getId(), version, servicePacket.getClient(), intents);
        ServiceManager.getInstance().getService(servicePacket.getId()).keepAlive();

        response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS);
        NetworkManager.getInstance().send(new C2SKeepAlivePacket(ServiceManager.getInstance().getService(servicePacket.getId())));
    }
}
