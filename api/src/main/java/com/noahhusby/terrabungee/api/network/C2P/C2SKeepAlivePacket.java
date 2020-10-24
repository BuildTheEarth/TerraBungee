package com.noahhusby.terrabungee.api.network.C2P;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.network.IC2SPacket;
import org.json.simple.JSONObject;

public class C2SKeepAlivePacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.keepAliveID;
    }

    @Override
    public void onMessage(TerraBungee instance, JSONObject data) {

    }
}
