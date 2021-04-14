package net.buildtheearth.api.network;

import com.google.gson.JsonObject;

public interface IS2CPacket {
    String getID();

    void onMessage(ServicePacket servicePacket, JsonObject data, Response response);
}
