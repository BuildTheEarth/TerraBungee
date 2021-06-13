/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - TerraBungeeProxyMain.java
 */

package com.noahhusby.terrabungee.proxy;

import com.noahhusby.terrabungee.proxy.commands.TerraBungeeAdminCommand;
import com.noahhusby.terrabungee.proxy.commands.TerraBungeeCommand;
import com.noahhusby.terrabungee.proxy.config.ConfigHandler;
import com.noahhusby.terrabungee.proxy.players.PlayerHandler;
import lombok.Getter;
import net.buildtheearth.terrabungee.client.TerraBungeeAPI;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;
import net.buildtheearth.terrabungee.common.services.ServiceType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.logging.Logger;

public class TerraBungeeProxy extends Plugin implements Listener {
    private static TerraBungeeProxy instance = null;
    @Getter
    private TerraBungeeClient terraBungee;
    public static Logger LOGGER;

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        instance = this;

        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyListener());
        ConfigHandler.getInstance();
        PlayerHandler.getInstance();

        terraBungee = TerraBungeeAPI.createService(ServiceType.PROXY, ConfigHandler.serviceID, ConfigHandler.controllerUrl);
        terraBungee.setAutoReconnect(true);
        terraBungee.connect();
        terraBungee.enableIntents(ServiceIntent.INSTANCE_UPDATE);

        terraBungee.addListener(new TBListener());

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
        terraBungee.discard();
    }

    public static TerraBungeeProxy getInstance() {
        return instance;
    }
}
