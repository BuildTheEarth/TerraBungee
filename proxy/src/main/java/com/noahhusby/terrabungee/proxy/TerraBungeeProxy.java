/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - TerraBungeeProxyMain.java
 */

package com.noahhusby.terrabungee.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noahhusby.terrabungee.proxy.commands.TerraBungeeAdminCommand;
import com.noahhusby.terrabungee.proxy.commands.TerraBungeeCommand;
import com.noahhusby.terrabungee.proxy.config.ConfigHandler;
import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.services.ServiceType;
import com.noahhusby.terrabungee.proxy.players.PlayerHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class TerraBungeeProxy extends Plugin implements Listener {
    private static TerraBungeeProxy instance = null;
    public static TerraBungee tb;
    public static Logger LOGGER;

	@Override
	public void onEnable() {
		LOGGER = getLogger();
		instance = this;

		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ConfigHandler.getInstance();
		PlayerHandler.getInstance();

		tb = new TerraBungee(ServiceType.PROXY, ConfigHandler.serviceID, ConfigHandler.controllerUrl);
		tb.setAutoReconnect(true);
		tb.connect();
		tb.enableIntents(ServiceIntent.INSTANCE_UPDATE);

		tb.addListener(new TBListener());

		getProxy().getPluginManager().registerCommand(this, new TerraBungeeCommand());
		getProxy().getPluginManager().registerCommand(this, new TerraBungeeAdminCommand());
	}

	@EventHandler
	public void onProxyJoin(ServerConnectEvent e) {
		/*
		if(e.getReason() == ServerConnectEvent.Reason.JOIN_PROXY && !ConfigHandler.queueServer.equals(""))
			e.getPlayer().connect(ProxyServer.getInstance().getServerInfo(ConfigHandler.queueServer));
		 */
	}

	
	@Override
	public void onDisable() {

		instance = null;
		tb.discard();
	}
	
	public static TerraBungeeProxy getInstance() {
		return instance;
	}
}
