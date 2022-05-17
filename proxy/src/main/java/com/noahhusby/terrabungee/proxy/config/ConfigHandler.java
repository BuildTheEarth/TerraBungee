/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - ConfigHandler.java
 */

package com.noahhusby.terrabungee.proxy.config;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigHandler {
    public static String queueServer = "queue";
    public static String controllerUrl = "";
    public static String serviceID = "";
    private static ConfigHandler instance;
    private final TerraBungeeProxy plugin = TerraBungeeProxy.getInstance();
    private Configuration config;

    private ConfigHandler() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

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

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

}
