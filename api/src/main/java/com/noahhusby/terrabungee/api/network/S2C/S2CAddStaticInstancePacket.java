package com.noahhusby.terrabungee.api.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.network.IC2SPacket;
import com.noahhusby.terrabungee.api.network.IS2CPacket;
import org.json.simple.JSONObject;

public class S2CAddStaticInstancePacket implements IS2CPacket {

    private final String id;
    private final String address;

    public S2CAddStaticInstancePacket(String id, String address) {
        this.id = id;
        this.address = address;
    }

    @Override
    public String getType() {
        return Constants.addStaticInstanceID;
    }

    @Override
    public JSONObject getMessage(TerraBungee instance, JSONObject data) {
        data.put("id", id);
        data.put("address", address);
        return data;
    }
}
