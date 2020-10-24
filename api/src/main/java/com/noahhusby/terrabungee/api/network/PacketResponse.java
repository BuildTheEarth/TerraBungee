package com.noahhusby.terrabungee.api.network;

import org.json.simple.JSONObject;

public interface PacketResponse {
    void onResponse(Response.ResponseCode code, JSONObject response);
}
