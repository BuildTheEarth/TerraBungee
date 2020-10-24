package com.noahhusby.terrabungee.api.network.C2P;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.network.IC2SPacket;
import com.noahhusby.terrabungee.api.network.Response;
import org.json.simple.JSONObject;

public class C2SResponsePacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.packetResponseID;
    }

    @Override
    public void onMessage(TerraBungee instance, JSONObject data) {
        String salt = (String) data.get("salt");
        Response.ResponseCode code = Response.ResponseCode.valueOf((String) data.get("response_code"));
        instance.getNetworkManager().onResponsePacket(salt, code, data);
    }
}
