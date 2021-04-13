package net.buildtheearth.terrabungee.controller.network;

import com.google.gson.JsonObject;

public interface IS2CPacket {
    String getID();

    void onMessage(ServicePacket servicePacket, JsonObject data, Response response);
}
