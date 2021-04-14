package net.buildtheearth.terrabungee.controller.network.C2S;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

public class C2SServiceMessagePacket implements IC2SPacket {

    private final String from;
    private final String to;
    private final String message;

    public C2SServiceMessagePacket(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

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
