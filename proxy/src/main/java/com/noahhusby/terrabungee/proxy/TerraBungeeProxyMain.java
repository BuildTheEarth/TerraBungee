/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * TerraBungeeProxyMain.java
 */

package com.noahhusby.terrabungee.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.common.collect.Maps;

import com.noahhusby.terrabungee.api.NetworkManager;
import com.noahhusby.terrabungee.proxy.commands.TerraBungeeAdminCommand;
import com.noahhusby.terrabungee.proxy.commands.TerraBungeeCommand;
import com.noahhusby.terrabungee.proxy.config.ConfigHandler;
import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.services.Instance;
import com.noahhusby.terrabungee.api.services.ServiceType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

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
		tb.enableIntent(ServiceIntent.INSTANCE_UPDATE);

		getProxy().getPluginManager().registerCommand(this, new TerraBungeeCommand());
		getProxy().getPluginManager().registerCommand(this, new TerraBungeeAdminCommand());

		getProxy().getScheduler().schedule(this, new Runnable() {
			@Override
			public void run() {
				List<Instance> instances = new ArrayList<>();
				instances.addAll(tb.getInstanceManager().getInstances());

				Map<String, ServerInfo> removeServerInfo = Maps.newHashMap();
				removeServerInfo.putAll(ProxyServer.getInstance().getServers());

				for(Instance i : tb.getInstanceManager().getInstances()) {
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
		}, 0, 2, TimeUnit.SECONDS);
	}

	@EventHandler
	public void onProxyJoin(PostLoginEvent e) {
		for(Map.Entry<String, ServerInfo> s : ProxyServer.getInstance().getServers().entrySet()) {
			e.getPlayer().connect(s.getValue(), ServerConnectEvent.Reason.JOIN_PROXY);
			return;
		}
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}
	
	public static TerraBungeeProxyMain getInstance() {
		return instance;
	}
}
