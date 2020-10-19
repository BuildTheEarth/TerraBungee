/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * InstanceManager.java
 */

package com.noahhusby.TerraBungeeProxy.instances;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.noahhusby.TerraBungeeProxy.ServerHelper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstanceManager {
    private static InstanceManager instance;

    public static InstanceManager getInstance() {
        if(instance == null) instance = new InstanceManager();
        return instance;
    }

    private InstanceManager() {}

    private List<StaticRemoteInstance> instances = new ArrayList<>();

    public void onInstancePayload(JsonObject data) {
        List<StaticRemoteInstance> newInstances = new ArrayList<>();
        for(JsonElement ia : data.getAsJsonArray("instances")) {
            newInstances.add(StaticRemoteInstance.fromJSON(ia.getAsJsonObject()));
        }

        for(Map.Entry<String, ServerInfo> s : ProxyServer.getInstance().getServers().entrySet()) {
            boolean exists = false;
            for(StaticRemoteInstance i : newInstances) {
                if(i.getId().equalsIgnoreCase(s.getKey())) exists = true;
            }

            if(!exists)  {
                ServerHelper.removeServer(s.getKey());
            }
        }

        for(StaticRemoteInstance i : newInstances) {
            boolean exists = false;
            for(Map.Entry<String, ServerInfo> s : ProxyServer.getInstance().getServers().entrySet()) {
                if(s.getKey().equalsIgnoreCase(i.getId())) exists = true;
            }

            if(!exists) {
                ServerHelper.addServer(i.getId(), i.getAddress());
            }
        }

    }
}
