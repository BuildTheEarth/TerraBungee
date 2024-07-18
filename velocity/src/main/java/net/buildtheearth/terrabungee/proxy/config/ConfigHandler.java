/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeProxy - ConfigHandler.java
 */

package net.buildtheearth.terrabungee.proxy.config;

import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandler {
    private static ConfigHandler instance;

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    public static String queueServer = "queue";
    public static String controllerUrl = "";
    public static String serviceID = "";

    private final TerraBungeeProxy plugin = TerraBungeeProxy.getInstance();
    private ConfigurationNode config;

    private ConfigHandler() {

        Path path = TerraBungeeProxy.getDataDirectory().resolve("config.yml");
        if (!path.toFile().exists()) {
            TerraBungeeProxy.LOGGER.info("Creating a new config for you. Please configure settings in plugins/TerraBungeeProxy/config.yml before starting.");
            try (InputStream in = plugin.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(in, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(path).build();
            config = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        queueServer = config.getString("queue-server");
        controllerUrl = config.getString("controller-url");
        serviceID = config.getString("service-id");
    }

}
