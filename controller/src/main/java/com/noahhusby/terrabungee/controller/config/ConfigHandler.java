package com.noahhusby.terrabungee.controller.config;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.services.InstanceManager;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigHandler {
    private static ConfigHandler instance = null;

    public static ConfigHandler getInstance() {
        if(instance == null) instance = new ConfigHandler();
        return instance;
    }

    private ConfigHandler() {
        init();
    }

    private File staticInstanceFile;

    private Configuration config;

    private String category;
    Map<String, List<String>> categories = Maps.newHashMap();

    public static String host;
    public static int port;

    public static String botToken;
    public static String guildID;
    public static String channelID;

    private void init() {
        File configFile = new File(System.getProperty("user.dir"), "terrabungee.cfg");
        if(!configFile.exists())
            TerraBungeeController.logger.warning("Generating a new config file! Please fill out terrabungee.cfg before continuing.");

        staticInstanceFile = new File(System.getProperty("user.dir"), "instances.json");
        loadStaticInstances();

        config = new Configuration(configFile);

        config.load();

        cat("General", "General settings for the TerraBungee Controller");
        host = config.getString(prop("Host"), category, "127.0.0.1", "The IP address that the controller should run on.");
        port = config.getInt(prop("Port"), category, 7000, 0, 65535, "The port that the controller should run on.");

        cat("Discord", "Settings for the discord bot");
        botToken = config.getString(prop("Bot Token"), category, "", "The token of the discord bot.");
        guildID = config.getString(prop("Guild ID"), category, "", "The ID of the Discord Guild/Server that the bot should listen on.");
        channelID = config.getString(prop("Updates Channel ID"), category, "", "The ID of the Discord channel where TerraBungee updates should be sent.");

        order();

        config.save();
    }

    private String prop(String n) {
        categories.get(category).add(n);
        return n;
    }

    private void cat(String category, String comment) {
        this.category = category;
        if(!categories.containsKey(category)) {
            categories.put(category, new ArrayList<>());
        }
        config.addCustomCategoryComment(category, comment);
    }

    private void order() {
        config.setCategoryPropertyOrder(category, categories.get(category));
    }

    public void loadStaticInstances() {
        if (staticInstanceFile.exists())
        {
            String json = null;
            try
            {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting()
                        .create();
                json = FileUtils.readFileToString(staticInstanceFile, "UTF-8");
                InstanceManager.setInstance(gson.fromJson(json, InstanceManager.class));
                InstanceManager.getInstance();
                saveStaticInstances();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                System.err.println("\n" + json);
            }
            return;
        }

        InstanceManager.setInstance(new InstanceManager());
        InstanceManager.getInstance();
        saveStaticInstances();
    }

    public void saveStaticInstances() {
        try
        {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            FileUtils.writeStringToFile(staticInstanceFile, gson.toJson(InstanceManager.getInstance()), "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
