package net.buildtheearth.terrabungee.controller.network.proxy;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class C2PProxyBanDisconnectPacket implements IC2SPacket {

    private final String proxy;
    private final Punishment punishment;

    @Override
    public String getID() {
        return Constants.proxyBanDisconnectID;
    }

    @Override
    public void getMessage(JsonObject data) {
        data.add("punishment", TerraBungeeUtil.GSON.toJsonTree(punishment));
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(ServiceManager.getInstance().getService(proxy));
    }
}
