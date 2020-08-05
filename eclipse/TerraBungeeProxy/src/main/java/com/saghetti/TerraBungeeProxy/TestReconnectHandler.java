package com.saghetti.TerraBungeeProxy;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TestReconnectHandler implements ReconnectHandler {

	@Override
	public ServerInfo getServer(ProxiedPlayer player) {
		return ProxyServer.getInstance().getServerInfo("lobby1");
	}

	@Override
	public void setServer(ProxiedPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
