/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * P2CKeepAlivePacket.java
 */

package com.noahhusby.TerraBungeeProxy.network.P2C;

import com.noahhusby.TerraBungeeProxy.network.IP2CPacket;
import org.json.simple.JSONObject;

public class P2CKeepAlivePacket implements IP2CPacket {
    @Override
    public String getType() {
        return "keep_alive";
    }

    @Override
    public JSONObject getMessage(JSONObject data) {
        return data;
    }
}
