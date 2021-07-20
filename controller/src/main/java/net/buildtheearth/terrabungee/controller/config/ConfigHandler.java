package net.buildtheearth.terrabungee.controller.config;

import com.google.common.collect.Maps;
import com.noahhusby.lib.data.sql.Credentials;
import com.noahhusby.lib.data.sql.MySQL;
import com.noahhusby.lib.data.sql.structure.Structure;
import com.noahhusby.lib.data.sql.structure.Type;
import com.noahhusby.lib.data.storage.Storage;
import com.noahhusby.lib.data.storage.StorageHashMap;
import com.noahhusby.lib.data.storage.StorageList;
import com.noahhusby.lib.data.storage.StorageTreeMap;
import com.noahhusby.lib.data.storage.handlers.LocalStorageHandler;
import com.noahhusby.lib.data.storage.handlers.SQLStorageHandler;
import lombok.Getter;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ConfigHandler {
    private static ConfigHandler instance = null;

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    private ConfigHandler() {
        init();
    }

    private File staticInstanceFile;
    private File playerDataFile;
    private File punishmentFile;
    private File discordConfigFile;

    private Configuration config;

    private String category;
    Map<String, List<String>> categories = Maps.newHashMap();

    public static String host;
    public static int port;

    public static String sqlHost;
    public static int sqlPort;
    public static String sqlUser;
    public static String sqlPassword;
    public static String sqlDb;
    public static boolean localEnabled;

    //TODO: God no
    public static String botToken = "";
    public static String guildID;
    public static String channelID;

    @Getter
    private Map<String, Storage> storageMap = Maps.newHashMap();

    private void init() {
        File configFile = new File(System.getProperty("user.dir"), "terrabungee.cfg");
        if (!configFile.exists()) {
            TerraBungeeController.logger.warning("Generating a new config file! Please fill out terrabungee.cfg before continuing.");
        }

        File localDb = new File(System.getProperty("user.dir"), "local");
        if (!localDb.exists()) {
            localDb.mkdir();
        }

        staticInstanceFile = new File(localDb, "instances.json");
        playerDataFile = new File(localDb, "players.json");
        discordConfigFile = new File(localDb, "discord.json");
        punishmentFile = new File(localDb, "punishments.json");

        config = new Configuration(configFile);
        loadData();
    }

    /**
     * Reloads all data/data fields. Called upon startup or reload
     */
    public void loadData() {
        config.load();

        cat("General", "General settings for the TerraBungee Controller");
        host = config.getString(prop("Host"), category, "127.0.0.1", "The IP address that the controller should run on.");
        port = config.getInt(prop("Port"), category, 7000, 0, 65535, "The port that the controller should run on.");

        order();

        cat("MySQL Database", "Settings for the MySQL Database");
        sqlHost = config.getString(prop("Host"), category, "127.0.0.1", "The host IP for the database.");
        sqlPort = config.getInt(prop("Port"), category, 3306, 0, 65535, "The port for the database.");
        sqlUser = config.getString(prop("Username"), category, "", "The username for the database.");
        sqlPassword = config.getString(prop("Password"), category, "", "The password for the database.");
        sqlDb = config.getString(prop("Database"), category, "", "The name of the database.");
        localEnabled = config.getBoolean(prop("Local Storage"), category, false, "Should data be stored locally as well");

        order();

        cat("Discord", "Settings for the discord bot");
        botToken = config.getString(prop("Bot Token"), category, "", "The token of the discord bot.");
        guildID = config.getString(prop("Guild ID"), category, "", "The ID of the Discord Guild/Server that the bot should listen on.");
        channelID = config.getString(prop("Updates Channel ID"), category, "", "The ID of the Discord channel where TerraBungee updates should be sent.");

        order();
        if (config.hasChanged()) {
            config.save();
        }

        Storage playerData = PlayerManager.getInstance().getPlayers();
        playerData.destroy();
        ((StorageHashMap) playerData).clear();

        Storage punishmentData = PlayerManager.getInstance().getPunishments();
        punishmentData.destroy();
        ((StorageHashMap) playerData).clear();

        Storage staticInstanceData = InstanceManager.getInstance().getStaticInstances();
        staticInstanceData.destroy();
        ((StorageTreeMap) staticInstanceData).clear();

        Storage discordConfigData = DiscordManager.getInstance().getDiscordConfigs();
        discordConfigData.clearHandlers();
        ((StorageList) discordConfigData).clear();

        if (localEnabled) {
            playerData.registerHandler(new LocalStorageHandler(playerDataFile));
            playerData.registerHandler(new LocalStorageHandler(punishmentFile));
            staticInstanceData.registerHandler(new LocalStorageHandler(staticInstanceFile));
            discordConfigData.registerHandler(new LocalStorageHandler(discordConfigFile));
        }

        {
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(
                    new Credentials(sqlHost, sqlPort, sqlUser, sqlPassword, sqlDb)), "Players",
                    Structure.builder()
                            .add("UUID", Type.TEXT)
                            .add("Name", Type.TEXT)
                            .add("Attributes", Type.TEXT)
                            .add("DiscordID", Type.TEXT)
                            .add("LastSeen", Type.BIGINT)
                            .repair(true)
                            .build()
            );
            sqlStorageHandler.setPriority(100);
            playerData.registerHandler(sqlStorageHandler);
        }

        {
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(
                    new Credentials(sqlHost, sqlPort, sqlUser, sqlPassword, sqlDb)), "Punishments",
                    Structure.builder()
                            .add("Id", Type.INT)
                            .add("Type", Type.TEXT)
                            .add("Staff", Type.TEXT)
                            .add("Player", Type.TEXT)
                            .add("Start", Type.TEXT)
                            .add("End", Type.TEXT)
                            .add("Reason", Type.TEXT)
                            .add("History", Type.TEXT)
                            .repair(true)
                            .build()
            );
            sqlStorageHandler.setPriority(100);
            punishmentData.registerHandler(sqlStorageHandler);
        }

        {
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(
                    new Credentials(sqlHost, sqlPort, sqlUser, sqlPassword, sqlDb)), "StaticInstances",
                    Structure.builder()
                            .add("Id", Type.TEXT)
                            .add("Address", Type.TEXT)
                            .repair(true)
                            .build()
            );
            sqlStorageHandler.setPriority(100);
            staticInstanceData.registerHandler(sqlStorageHandler);
        }

        {
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(
                    new Credentials(sqlHost, sqlPort, sqlUser, sqlPassword, sqlDb)), "DiscordConfig",
                    Structure.builder()
                            .add("GuildID", Type.TEXT)
                            .add("NotificationChannel", Type.TEXT)
                            .add("AdminRoles", Type.TEXT)
                            .add("ModeratorRoles", Type.TEXT)
                            .add("StandardRoles", Type.TEXT)
                            .repair(true)
                            .build()
            );
            sqlStorageHandler.setPriority(100);
            discordConfigData.registerHandler(sqlStorageHandler);
        }

        storageMap.put("players", playerData);
        storageMap.put("punishment", punishmentData);
        storageMap.put("instance", staticInstanceData);
        storageMap.put("discord_config", discordConfigData);

        TerraBungeeController.getInstance().getGeneralThreads().schedule(() -> {
            playerData.load();
            playerData.setAutoSave(10, TimeUnit.SECONDS);
            punishmentData.load();
            punishmentData.setAutoSave(10, TimeUnit.SECONDS);
            staticInstanceData.setAutoLoad(10, TimeUnit.SECONDS);
            staticInstanceData.setAutoSave(10, TimeUnit.SECONDS);
            discordConfigData.setAutoLoad(10, TimeUnit.SECONDS);
            discordConfigData.setAutoSave(10, TimeUnit.SECONDS);
        }, 2, TimeUnit.SECONDS);
    }

    /**
     * Reloads the controller
     */
    public void reload() {
        TerraBungeeController.logger.info("Reloading the controller!");
        loadData();
    }

    /**
     * Migrates data from the local storage to the database
     */
    public void migrate() {
        PlayerManager.getInstance().getPlayers().migrate(0);
        InstanceManager.getInstance().getStaticInstances().migrate(0);
        DiscordManager.getInstance().getDiscordConfigs().migrate(0);
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
