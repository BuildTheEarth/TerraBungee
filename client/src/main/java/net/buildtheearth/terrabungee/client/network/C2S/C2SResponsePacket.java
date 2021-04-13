/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - C2SResponsePacket.java
 */

package net.buildtheearth.terrabungee.client.network.C2S;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.client.network.ResponseRequest;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.network.Response;

public class C2SResponsePacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.responseID;
    }

    @Override
    public void onMessage(TerraBungeeClient instance, JsonObject data) {
        String salt = data.get("salt").getAsString();
        Response.ResponseCode responseCode = Response.ResponseCode.valueOf(data.get("response_code").getAsString());
        JsonObject responseData = data.getAsJsonObject("response_data");
        ResponseRequest request = instance.getNetworkManager().getResponseRequests().get(salt);
        if (request != null) {
            request.getFuture().complete(new Response(responseCode, responseData));
            instance.getNetworkManager().getResponseRequests().remove(salt);
        }
    }
}
