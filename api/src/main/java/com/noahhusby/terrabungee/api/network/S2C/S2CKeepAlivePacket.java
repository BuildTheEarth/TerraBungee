/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * P2CKeepAlivePacket.java
 */

package com.noahhusby.terrabungee.api.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.api.network.IS2CPacket;
import org.json.simple.JSONObject;

public class S2CKeepAlivePacket implements IS2CPacket {
    @Override
    public String getType() {
        return Constants.keepAliveID;
    }

    @Override
    public JSONObject getMessage(TerraBungee instance, JSONObject data) {
        data.put("type", instance.getServiceType().name());
        data.put("intents", TerraBungeeUtil.intentsToArray(instance.getIntents()).toJSONString());
        return data;
    }
}
