/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - C2SServiceMessagePacket.java
 */

package net.buildtheearth.terrabungee.client.network.C2S;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.EventListener;
import net.buildtheearth.terrabungee.client.events.message.ServiceMessageEvent;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;

public class C2SServiceMessagePacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.serviceMessageID;
    }

    @Override
    public void onMessage(TerraBungeeClient instance, JsonObject data) {
        JsonObject message = TerraBungeeUtil.parse(data.get("message").getAsString());
        String senderId = data.get("from").getAsString();
        for (EventListener listener : instance.getListeners()) {
            listener.onServiceMessage(new ServiceMessageEvent(instance, senderId, message));
        }
    }
}
