/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * ConfigHandler.java
 */

package com.noahhusby.terrabungee.proxy.config;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxyMain;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigHandler {
    private static ConfigHandler instance;

    public static ConfigHandler getInstance() {
        if(instance == null) instance = new ConfigHandler();
        return instance;
    }

    public static String queueServer = "queue";
    public static String controllerUrl = "";
    public static String serviceID = "";

    private final TerraBungeeProxyMain plugin = TerraBungeeProxyMain.getInstance();
    private Configuration config;

    private ConfigHandler() {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.getLogger().info("Creating a new config for you. Please configure settings in plugins/TerraBungeeProxy/config.yml before starting.");
            try (InputStream in = plugin.getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //return;
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        queueServer = config.getString("queue-server");
        controllerUrl = config.getString("controller-url");
        serviceID = config.getString("service-id");
    }

}
