/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - ServerHelper.java
 */

package com.noahhusby.terrabungee.proxy;

import java.net.InetSocketAddress;

import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ServerHelper {
	public static void addServer(String instanceId, String address) {
		if (ProxyServer.getInstance().getServerInfo(instanceId) != null) {
			TerraBungeeProxy.getInstance().getLogger().info("Instance " + instanceId + " tried to be created but already existed?! Ignoring request.");
			return;
		}
		InetSocketAddress inetSocketAddress = TerraBungeeUtil.makeInetSocketAddress(address);
		ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(
			instanceId, inetSocketAddress,
			"A TerraBungee Instance - " + instanceId, false
		);
		ProxyServer.getInstance().getServers().put(instanceId, serverInfo);
		TerraBungeeProxy.getInstance().getLogger().info("Added instance " + instanceId + " on address " + address);
	}
	
	public static void removeServer(String instanceId) {
		if (ProxyServer.getInstance().getServerInfo(instanceId) == null) {
			TerraBungeeProxy.getInstance().getLogger().info("Instance " + instanceId + " tried to be deleted but didn't exist?! Ignoring request.");
			return;
		}
		ProxyServer.getInstance().getServers().remove(instanceId);
		TerraBungeeProxy.getInstance().getLogger().info("Removed instance " + instanceId);
	}
}
