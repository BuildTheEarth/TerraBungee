package com.noahhusby.terrabungee.api.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.network.IS2CPacket;
import org.json.simple.JSONObject;

public class S2CRemoveStaticInstancePacket implements IS2CPacket {

    private final String id;

    public S2CRemoveStaticInstancePacket(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return Constants.removeStaticInstanceID;
    }

    @Override
    public JSONObject getMessage(TerraBungee instance, JSONObject data) {
        data.put("id", id);
        return data;
    }
}
