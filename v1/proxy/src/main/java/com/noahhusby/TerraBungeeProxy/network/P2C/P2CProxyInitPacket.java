/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * P2CProxyInitPacket.java
 */

package com.noahhusby.TerraBungeeProxy.network.P2C;

import com.noahhusby.TerraBungeeProxy.Constants;
import com.noahhusby.TerraBungeeProxy.network.IP2CPacket;
import org.json.simple.JSONObject;

public class P2CProxyInitPacket implements IP2CPacket {
    @Override
    public String getType() {
        return Constants.proxyInitID;
    }

    @Override
    public JSONObject getMessage(JSONObject data) {
        return data;
    }
}
