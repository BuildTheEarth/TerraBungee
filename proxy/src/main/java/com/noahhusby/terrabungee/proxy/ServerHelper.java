/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * ServerHelper.java
 */

package com.noahhusby.terrabungee.proxy;

import java.net.InetSocketAddress;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ServerHelper {
	public static void addServer(String instanceId, String address) {
		if (ProxyServer.getInstance().getServerInfo(instanceId) != null) {
			TerraBungeeProxyMain.getInstance().getLogger().info("Instance " + instanceId + " tried to be created but already existed?! Ignoring request.");
			return;
		}
		InetSocketAddress inetSocketAddress = Utility.makeInetSocketAddressFromString(address);
		ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(
			instanceId, inetSocketAddress,
			"A TerraBungee Instance - " + instanceId, false
		);
		ProxyServer.getInstance().getServers().put(instanceId, serverInfo);
		TerraBungeeProxyMain.getInstance().getLogger().info("Added instance " + instanceId + " on address " + address);
	}
	
	public static void removeServer(String instanceId) {
		if (ProxyServer.getInstance().getServerInfo(instanceId) == null) {
			TerraBungeeProxyMain.getInstance().getLogger().info("Instance " + instanceId + " tried to be deleted but didn't exist?! Ignoring request.");
			return;
		}
		ProxyServer.getInstance().getServers().remove(instanceId);
		TerraBungeeProxyMain.getInstance().getLogger().info("Removed instance " + instanceId);
	}
}
