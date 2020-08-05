package com.saghetti.TerraBungeeProxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TerraBungeeProxyMain extends Plugin {
	Configuration config;
    JedisPool jedisPool = null;
    Jedis subJedis = null;
    Thread subThread;
    private static TerraBungeeProxyMain instance = null;

	@Override
	public void onEnable() {
		instance = this;
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try {
        	config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        Logger logger = getLogger();
        logger.info("Connecting to Redis...");
        logger.info("Address: " + config.getSection("redis").getString("host") + ":" + config.getSection("redis").getInt("port"));
		jedisPool = new JedisPool(config.getSection("redis").getString("host"), config.getSection("redis").getInt("port"));
		subJedis = jedisPool.getResource();
		logger.info("Starting listener thread");
		subThread = new Thread() {
			@Override
			public void run() {
				// TODO: allow channel prefix in config
				System.out.println("Subscribing to tb-controller-calls");
				subJedis.subscribe(new ControllerCallsListener(), "tb-controller-calls");
			}
		};
		subThread.start();
		getProxy().setReconnectHandler(new TestReconnectHandler());
	}
	
	@Override
	public void onDisable() {
		instance = null;
		subThread.stop(); // TODO: make better
		subJedis.disconnect();
		jedisPool.destroy();
		subJedis = null;
		jedisPool = null;
	}
	
	public static TerraBungeeProxyMain getInstance() {
		return instance;
	}
}
