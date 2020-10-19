/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * TerraBungeeProxyMain.java
 */

package com.noahhusby.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.noahhusby.TerraBungeeAPI.Network;

import com.noahhusby.proxy.config.ConfigHandler;
import com.noahhusby.terrabungee.api.ServiceIntent;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.services.ServiceType;
import net.md_5.bungee.api.plugin.Plugin;

public class TerraBungeeProxyMain extends Plugin {
    Thread subThread;
    private static TerraBungeeProxyMain instance = null;
    private Logger logger;
    Network network;
    List<String> lastInstanceList = new ArrayList<String>();

	@Override
	public void onEnable() {
		logger = getLogger();
		instance = this;
		ConfigHandler.getInstance();

		TerraBungee tb = TerraBungee.getInstance();
		tb.createService("127.0.0.1:7000", ServiceType.PROXY, "proxy2");
		/*
        logger.info("Connecting to controller...");
        try {
			network = new Network(ConfigHandler.controllerUrl);
		} catch (NetworkException e) {
			e.printStackTrace();
		}
        getProxy().getPluginManager().registerCommand(this, new DebugCommand());
		getProxy().setReconnectHandler(new TestReconnectHandler());
		// instance polling task
		getProxy().getScheduler().schedule(this, new Runnable() {
			@Override
			public void run() {
				List<RemoteInstance> remoteInstancesCurrent = new ArrayList<RemoteInstance>();
				// filter only online instances
				for (RemoteInstance instance : network.getAllInstancesStatic()) {
					if (!instance.isOnline()) continue;
					remoteInstancesCurrent.add(instance);
				}
				// add new instances that weren't in the list previously
				for (RemoteInstance instance : remoteInstancesCurrent) {
					if (!lastInstanceList.contains(instance.getId())) {
						logger.info("Adding new instance " + instance.getId());
						ServerHelper.addServer(instance.getId(), instance.getAddress());
						lastInstanceList.add(instance.getId());
					}
				}
				// remove instances that are no longer online
				// TODO: add logic to move players to a different server before one is deleted!
				List<String> pendingInstancesRemove = new ArrayList<String>();
				List<String> currentInstancesAsStrings = new ArrayList<String>();
				for (RemoteInstance instance : remoteInstancesCurrent) {
					currentInstancesAsStrings.add(instance.getId());
				}
				
				// remove instances that are no longer online
				for (String instanceId : lastInstanceList) {
					if (!currentInstancesAsStrings.contains(instanceId)) {
						logger.info("Removing instance " + instanceId);
						ServerHelper.removeServer(instanceId);
						pendingInstancesRemove.add(instanceId);
					}
				}
				
				for (String instanceId : pendingInstancesRemove) {
					lastInstanceList.remove(instanceId);
				}
			}
		}, 0, 2000, TimeUnit.MILLISECONDS);

		 */
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}
	
	public static TerraBungeeProxyMain getInstance() {
		return instance;
	}
}
