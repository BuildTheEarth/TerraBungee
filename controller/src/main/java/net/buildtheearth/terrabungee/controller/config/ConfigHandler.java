package net.buildtheearth.terrabungee.controller.config;

import com.google.common.collect.Maps;
import com.noahhusby.lib.application.config.Configuration;
import com.noahhusby.lib.data.sql.MySQL;
import com.noahhusby.lib.data.sql.structure.Structure;
import com.noahhusby.lib.data.sql.structure.Type;
import com.noahhusby.lib.data.storage.Storage;
import com.noahhusby.lib.data.storage.StorageHashMap;
import com.noahhusby.lib.data.storage.StorageTreeMap;
import com.noahhusby.lib.data.storage.handlers.LocalStorageHandler;
import com.noahhusby.lib.data.storage.handlers.SQLStorageHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;

import java.io.File;
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
    private File discordGuildFile;
    private File discordBotFile;

    @Getter
    private final Map<String, Storage> storageMap = Maps.newHashMap();

    private void init() {
        File localDb = new File(TerraBungee.getInstance().getFolder(), "local");
        if (!localDb.exists()) {
            localDb.mkdir();
        }

        staticInstanceFile = new File(localDb, "instances.json");
        playerDataFile = new File(localDb, "players.json");
        discordGuildFile = new File(localDb, "discord_guilds.json");
        punishmentFile = new File(localDb, "punishments.json");
        discordBotFile = new File(localDb, "discord_bots.json");

        load();
    }

    @SneakyThrows
    public void load() {
        Configuration configuration = Configuration.of(TerraBungeeConfig.class);
        configuration.sync(TerraBungeeConfig.class);
        loadHandlers();
    }

    @SneakyThrows
    public void unload() {
        PlayerManager.getInstance().getPlayers().close();
        PlayerManager.getInstance().getPunishments().close();
        InstanceManager.getInstance().getStaticInstances().close();
        DiscordManager.getInstance().getGuildConfigs().close();
        DiscordManager.getInstance().getBotConfigs().close();
    }

    @SneakyThrows
    public void loadHandlers() {
        Storage playerData = PlayerManager.getInstance().getPlayers();
        playerData.close();
        ((StorageHashMap<?, ?>) playerData).clear();

        Storage punishmentData = PlayerManager.getInstance().getPunishments();
        punishmentData.close();
        ((StorageHashMap<?, ?>) playerData).clear();

        Storage staticInstanceData = InstanceManager.getInstance().getStaticInstances();
        staticInstanceData.close();
        ((StorageTreeMap<?, ?>) staticInstanceData).clear();

        Storage discordGuildConfigData = DiscordManager.getInstance().getGuildConfigs();
        discordGuildConfigData.clearHandlers();
        ((StorageHashMap<?, ?>) discordGuildConfigData).clear();

        Storage discordBotConfigData = DiscordManager.getInstance().getBotConfigs();
        discordBotConfigData.clearHandlers();
        ((StorageHashMap<?, ?>) discordBotConfigData).clear();

        TerraBungeeConfig.DatabaseOptions databaseOptions = TerraBungeeConfig.database;

        if (databaseOptions.localStorage) {
            playerData.registerHandler(new LocalStorageHandler(playerDataFile));
            playerData.registerHandler(new LocalStorageHandler(punishmentFile));
            staticInstanceData.registerHandler(new LocalStorageHandler(staticInstanceFile));
            discordGuildConfigData.registerHandler(new LocalStorageHandler(discordGuildFile));
            discordBotConfigData.registerHandler(new LocalStorageHandler(discordBotFile));
        }

        {
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(databaseOptions.toCredentials()), "Players",
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
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(databaseOptions.toCredentials()), "Punishments",
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
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(databaseOptions.toCredentials()), "StaticInstances",
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
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(databaseOptions.toCredentials()), "DiscordGuilds",
                    Structure.builder()
                            .add("GuildID", Type.TEXT)
                            .add("BotID", Type.INT)
                            .add("NotificationChannel", Type.TEXT)
                            .add("StaffRoles", Type.TEXT)
                            .repair(true)
                            .build()
            );
            sqlStorageHandler.setPriority(100);
            discordGuildConfigData.registerHandler(sqlStorageHandler);
        }

        {
            SQLStorageHandler sqlStorageHandler = new SQLStorageHandler(new MySQL(databaseOptions.toCredentials()), "DiscordBots",
                    Structure.builder()
                            .add("Id", Type.INT)
                            .add("Name", Type.TEXT)
                            .add("Token", Type.TEXT)
                            .repair(true)
                            .build()
            );
            sqlStorageHandler.setPriority(100);
            discordBotConfigData.registerHandler(sqlStorageHandler);
        }

        storageMap.put("players", playerData);
        storageMap.put("punishment", punishmentData);
        storageMap.put("instance", staticInstanceData);
        storageMap.put("discord_config", discordGuildConfigData);
        storageMap.put("discord_bots", discordBotConfigData);

        TerraBungeeController.getInstance().getGeneralThreads().schedule(() -> {
            playerData.load();
            playerData.setAutoSave(5, TimeUnit.MINUTES);
            punishmentData.load();
            punishmentData.setAutoSave(5, TimeUnit.SECONDS);
            staticInstanceData.setAutoSave(5, TimeUnit.SECONDS);
            discordGuildConfigData.load();
            discordGuildConfigData.save();
            discordBotConfigData.load();
            discordBotConfigData.save();
        }, 2, TimeUnit.SECONDS);
    }

    /**
     * Reloads the controller
     */
    public void reload() {
        TerraBungeeController.logger.info("Reloading the controller!");
        load();
    }

    /**
     * Migrates data from the local storage to the database
     */
    public void migrate() {
        PlayerManager.getInstance().getPlayers().migrate(0);
        InstanceManager.getInstance().getStaticInstances().migrate(0);
        DiscordManager.getInstance().getGuildConfigs().migrate(0);
    }
}
