/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - ConfigHandler.java
 */

package net.buildtheearth.terrabungee.proxy.config;

import com.google.common.collect.Maps;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.minecraftforge.common.config.Configuration;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private Configuration config;

    private String category;
    Map<String, List<String>> categories = Maps.newHashMap();

    private ConfigHandler() {

        Path path = TerraBungeeProxy.getDataDirectory().resolve("config.cfg");
        if (!path.toFile().exists()) {
            TerraBungeeProxy.logger.info("Creating a new config for you. Please configure settings in " + TerraBungeeProxy.getDataDirectory().toString() + "/config.cfg before starting.");

            // Create the directory
            boolean created = TerraBungeeProxy.getDataDirectory().toFile().exists() || TerraBungeeProxy.getDataDirectory().toFile().mkdir();
            if (!created) {
                TerraBungeeProxy.logger.info("Unable to create TerraBungeeProxy directory. Please configure settings in " + TerraBungeeProxy.getDataDirectory().toString() + "/config.cfg before starting.");
                return;
            }
        }

        config = new Configuration(path.toFile());
        loadData();
    }

    private void loadData() {
        config.load();

        cat("General", "General Settings");
        controllerUrl = config.getString(prop("controller-url"), category, "127.0.0.1", "The URL that the controller can be accessed from.");
        queueServer = config.getString(prop("queue-server"), category, "Hub", "The name of the queue server that players should be sent to when first joining or being queued.");
        serviceID = config.getString(prop("service-id"), category, "proxy", "The unique name of the proxy server in the controller.");

        order();

        if (config.hasChanged()) {
            config.save();
        }
    }

    /**
     * Reloads the controller
     */
    public void reload() {
        TerraBungeeProxy.logger.info("Reloading the config!");
        loadData();
    }

    private String prop(String n) {
        categories.get(category).add(n);
        return n;
    }

    private void cat(String category, String comment) {
        this.category = category;
        if (!categories.containsKey(category)) {
            categories.put(category, new ArrayList<>());
        }
        config.addCustomCategoryComment(category, comment);
    }

    private void order() {
        config.setCategoryPropertyOrder(category, categories.get(category));
    }

}
