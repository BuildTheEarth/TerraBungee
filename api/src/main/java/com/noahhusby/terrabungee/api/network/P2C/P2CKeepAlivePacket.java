/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * P2CKeepAlivePacket.java
 */

package com.noahhusby.terrabungee.api.network.P2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.network.IP2CPacket;
import org.json.simple.JSONObject;

public class P2CKeepAlivePacket implements IP2CPacket {
    @Override
    public String getType() {
        return Constants.keepAliveID;
    }

    @Override
    public JSONObject getMessage(JSONObject data) {
        return data;
    }
}
