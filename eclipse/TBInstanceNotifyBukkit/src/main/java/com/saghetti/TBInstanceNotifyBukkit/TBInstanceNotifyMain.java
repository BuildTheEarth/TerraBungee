package com.saghetti.TBInstanceNotifyBukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig; 

public class TBInstanceNotifyMain extends JavaPlugin  {
	final Logger logger = getLogger();
	JedisPool jedisPool = null;
	Jedis subJedis = null;
	Jedis pubJedis = null;
	FileConfiguration config = null;
	String instanceId = "";
	String channelPrefix = "";
	String address = "";
	String redisAddress = "";
	Thread subThread;
	
    @SuppressWarnings("unchecked")
	@Override
    public void onEnable() {
    	Path rootServerFolder = this.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().toPath();
    	try {
			instanceId = new String(Files.readAllBytes(rootServerFolder.resolve("tb_info/id.txt")));
			redisAddress = new String(Files.readAllBytes(rootServerFolder.resolve("tb_info/redisaddr.txt")));
			channelPrefix = new String(Files.readAllBytes(rootServerFolder.resolve("tb_info/chprefix.txt")));
			address = new String(Files.readAllBytes(rootServerFolder.resolve("tb_info/address.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	logger.info("Connecting to Redis...");
    	jedisPool = new JedisPool(new JedisPoolConfig(),redisAddress);
    	pubJedis = jedisPool.getResource();
    	JSONObject obj = new JSONObject();
    	obj.put("sender","instance:" + instanceId);
    	obj.put("recipient","*");
    	obj.put("type","instance-online");
    	JSONObject contentsObj = new JSONObject();
    	contentsObj.put("address", address);
    	obj.put("data",contentsObj);
    	pubJedis.publish(channelPrefix + "tb-controller-calls",obj.toString());
    	logger.info("Now online!");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onDisable() {
    	JSONObject obj = new JSONObject();
    	obj.put("sender",instanceId);
    	obj.put("recipient","*");
    	obj.put("type","instance-offline");
    	obj.put("data",null);
    	pubJedis.publish(channelPrefix + "tb-controller-calls",obj.toString());
    	logger.info("Now offline!");
    }
}
