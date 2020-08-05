package com.saghetti.TerraBungeeProxy;

import java.net.InetSocketAddress;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import redis.clients.jedis.JedisPubSub;

public class ControllerCallsListener extends JedisPubSub {
    public void onMessage(String channel, String message) {
    	// parse message
    	System.out.println(message);
    	if (!channel.equalsIgnoreCase("tb-controller-calls")) {
    		System.out.println("Wrong channel smh");
    		return;
    	}
    	try {
			JSONObject parsedMessageData = (JSONObject) new JSONParser().parse(message);
			String messageType = (String) parsedMessageData.get("type");
			if (messageType.equalsIgnoreCase("instance-online")) {
				String instanceId = (String) parsedMessageData.get("sender");
				instanceId = instanceId.substring(9); // strip instance: prefix
				String address = (String) ((JSONObject) parsedMessageData.get("data")).get("address");
				if (ProxyServer.getInstance().getServerInfo(instanceId) != null) {
					TerraBungeeProxyMain.getInstance().getLogger().info("Instance " + instanceId + " tried to be created but already existed?! Ignoring request.");
					return;
				}
				TerraBungeeProxyMain.getInstance().getLogger().info("Added instance " + instanceId + " on address " + address);
				InetSocketAddress inetSocketAddress = Utility.makeInetSocketAddressFromString(address);
				ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(
					instanceId, inetSocketAddress,
					"A TerraBungee Instance - " + instanceId, false
				);
				ProxyServer.getInstance().getServers().put(instanceId, serverInfo);
			} else if (messageType.equalsIgnoreCase("instance-offline")) {
				String instanceId = (String) parsedMessageData.get("sender");
				instanceId = instanceId.substring(9); // strip instance: prefix
				if (ProxyServer.getInstance().getServerInfo(instanceId) == null) {
					TerraBungeeProxyMain.getInstance().getLogger().info("Instance " + instanceId + " tried to be deleted but didn't exist?! Ignoring request.");
					return;
				}
				ProxyServer.getInstance().getServers().remove(instanceId);
				TerraBungeeProxyMain.getInstance().getLogger().info("Removed instance " + instanceId);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }
}
