/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - TerraBungeeProxyMain.java
 */

package com.noahhusby.terrabungee.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.common.collect.Maps;

import com.noahhusby.terrabungee.api.events.EventListener;
import com.noahhusby.terrabungee.api.events.controller.ControllerConnectedEvent;
import com.noahhusby.terrabungee.api.events.service.InstanceUpdateEvent;
import com.noahhusby.terrabungee.api.network.PacketResponse;
import com.noahhusby.terrabungee.api.network.Response;
import com.noahhusby.terrabungee.proxy.commands.TerraBungeeAdminCommand;
import com.noahhusby.terrabungee.proxy.commands.TerraBungeeCommand;
import com.noahhusby.terrabungee.proxy.config.ConfigHandler;
import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.api.services.ServiceType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.json.simple.JSONObject;

public class TerraBungeeProxyMain extends Plugin implements Listener {
    private static TerraBungeeProxyMain instance = null;
    public static TerraBungee tb;
    private Logger logger;

	@Override
	public void onEnable() {
		logger = getLogger();
		instance = this;

		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ConfigHandler.getInstance();

		tb = new TerraBungee(ServiceType.PROXY, ConfigHandler.serviceID, ConfigHandler.controllerUrl);
		tb.setAutoReconnect(true);
		tb.connect();
		tb.enableIntent(ServiceIntent.INSTANCE_UPDATE);

		tb.addListener(new EventListener() {
			@Override
			public void onInstanceUpdate(InstanceUpdateEvent event) {
				List<Instance> instances = new ArrayList<>(event.getInstances());

				Map<String, ServerInfo> removeServerInfo = Maps.newHashMap();
				removeServerInfo.putAll(ProxyServer.getInstance().getServers());

				for(Instance i : event.getInstances()) {
					for(Map.Entry<String, ServerInfo> s : ProxyServer.getInstance().getServers().entrySet()) {
						if(s.getKey().equalsIgnoreCase(i.getId())) {
							removeServerInfo.remove(s.getKey(), s.getValue());
							instances.remove(i);
						}
					}
				}

				for(Instance i : instances) {
					ServerHelper.addServer(i.getId(), i.getAddress());
				}

				for(Map.Entry<String, ServerInfo> s : removeServerInfo.entrySet()) {
					ServerHelper.removeServer(s.getKey());
				}
			}
		});

		getProxy().getPluginManager().registerCommand(this, new TerraBungeeCommand());
		getProxy().getPluginManager().registerCommand(this, new TerraBungeeAdminCommand());
	}

	@EventHandler
	public void onProxyJoin(ServerConnectEvent e) {
		if(e.getReason() == ServerConnectEvent.Reason.JOIN_PROXY)
			e.getPlayer().connect(ProxyServer.getInstance().getServerInfo(ConfigHandler.queueServer));
	}
	
	@Override
	public void onDisable() {
		instance = null;
		tb.discard();
	}
	
	public static TerraBungeeProxyMain getInstance() {
		return instance;
	}
}
