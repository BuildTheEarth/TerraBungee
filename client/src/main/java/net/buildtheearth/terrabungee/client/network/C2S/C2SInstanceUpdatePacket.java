/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - C2SInstanceUpdatePacket.java
 */

package net.buildtheearth.terrabungee.client.network.C2S;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.service.InstanceUpdateEvent;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.services.Instance;

import java.util.ArrayList;
import java.util.List;

public class C2SInstanceUpdatePacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.instanceUpdateID;
    }

    @Override
    public void onMessage(TerraBungeeClient tb, JsonObject data) {
        JsonArray instanceArray = data.getAsJsonArray("instances");
        List<Instance> instances = new ArrayList<>();
        for (JsonElement o : instanceArray) {
            instances.add(TerraBungeeUtil.GSON.fromJson(o, Instance.class));
        }

        tb.triggerEvent(l -> l.onInstanceUpdate(new InstanceUpdateEvent(tb, instances)));
        tb.getInstanceManager().setInstances(instances);
    }
}