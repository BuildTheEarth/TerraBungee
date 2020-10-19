/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * P2CProxyInitPacket.java
 */

package com.noahhusby.terrabungee.api.network.P2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.api.network.IP2CPacket;
import org.json.simple.JSONObject;

public class P2CServiceInitPacket implements IP2CPacket {
    @Override
    public String getType() {
        return Constants.serviceInitID;
    }

    @Override
    public JSONObject getMessage(JSONObject data) {
        data.put("type", TerraBungee.getInstance().getServiceType().name());
        data.put("intents", TerraBungeeUtil.intentsToArray(TerraBungee.getInstance().getIntents()).toJSONString());
        return data;
    }
}
