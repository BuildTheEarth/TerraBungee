/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - TerraBungeeListener.java
 */

package com.noahhusby.terrabungee.proxy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.noahhusby.terrabungee.proxy.players.PlayerHandler;
import com.noahhusby.terrabungee.proxy.util.ProxyUtil;
import net.buildtheearth.terrabungee.client.events.EventListener;
import net.buildtheearth.terrabungee.client.events.player.OnlineCacheHitEvent;
import net.buildtheearth.terrabungee.client.events.service.InstanceUpdateEvent;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.instance.Instance;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Noah Husby
 */
public class TBListener extends EventListener {

    @Override
    public void onInstanceUpdate(InstanceUpdateEvent event) {
        List<Instance> instances = new ArrayList<>(event.getInstances());

        Map<String, ServerInfo> removeServerInfo = Maps.newHashMap();
        removeServerInfo.putAll(ProxyServer.getInstance().getServers());

        for (Instance i : event.getInstances()) {
            for (Map.Entry<String, ServerInfo> s : ProxyServer.getInstance().getServers().entrySet()) {
                if (s.getKey().equalsIgnoreCase(i.getId())) {
                    removeServerInfo.remove(s.getKey(), s.getValue());
                    instances.remove(i);
                }
            }
        }

        for (Instance i : instances) {
            if (i.getId().equals("Hub")) {
                continue;
            }
            ProxyUtil.addServer(i.getId(), i.getAddress().toString());
        }

        for (Map.Entry<String, ServerInfo> s : removeServerInfo.entrySet()) {
            if (s.getValue().getName().equals("Hub")) {
                continue;
            }
            ProxyUtil.removeServer(s.getKey());
        }
    }

    @Override
    public void onOnlineCacheHit(OnlineCacheHitEvent event) {
        List<String> playerNames = Lists.newArrayList();
        for (TBPlayer player : event.getPlayers()) {
            playerNames.add(player.getName());
        }
        PlayerHandler.getInstance().setOnlinePlayerNames(playerNames);
    }
}


