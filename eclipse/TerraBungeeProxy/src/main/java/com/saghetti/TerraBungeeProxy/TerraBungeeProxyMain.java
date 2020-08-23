package com.saghetti.TerraBungeeProxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.saghetti.TerraBungeeAPI.Network;
import com.saghetti.TerraBungeeAPI.NetworkException;
import com.saghetti.TerraBungeeAPI.RemoteInstance;
import com.saghetti.TerraBungeeAPI.StaticRemoteInstance;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
public class TerraBungeeProxyMain extends Plugin {
	Configuration config;
    Thread subThread;
    private static TerraBungeeProxyMain instance = null;
    public String queueServer = "queue";
    Logger logger;
    Network network;
    List<String> lastInstanceList = new ArrayList<String>();

	@Override
	public void onEnable() {
		logger = getLogger();
		instance = this;
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
        	logger.info("Creating a new config for you. Please configure settings in plugins/TerraBungeeProxy/config.yml before starting.");
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //return;
        }
        
        try {
        	config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        queueServer = config.getString("queue-server");
        logger.info("Connecting to controller...");
        try {
			network = new Network(config.getString("controller-url"));
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
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}
	
	public static TerraBungeeProxyMain getInstance() {
		return instance;
	}
}
