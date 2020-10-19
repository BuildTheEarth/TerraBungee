package com.noahhusby.terrabungee.controller.network;

import org.json.simple.JSONObject;

public interface IS2CPacket {
    String getID();
    void onMessage(ServicePacket servicePacket, JSONObject data);
}
