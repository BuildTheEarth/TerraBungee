package net.buildtheearth.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

@RequiredArgsConstructor
public class C2SServiceMessagePacket implements IC2SPacket {

    private final String from;
    private final String to;
    private final String message;

    @Override
    public String getID() {
        return Constants.serviceMessageID;
    }

    @Override
    public void getMessage(JsonObject data) {
        data.addProperty("message", message);
        data.addProperty("from", from);
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(ServiceManager.getInstance().getService(to));
    }
}
