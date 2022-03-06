package net.buildtheearth.terrabungee.controller.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.noahhusby.lib.application.config.Configuration;
import com.noahhusby.lib.application.config.exception.ClassNotConfigException;
import com.noahhusby.lib.data.storage.Storage;
import com.noahhusby.lib.data.storage.StorageHashMap;
import com.noahhusby.lib.data.storage.StorageTreeMap;
import com.noahhusby.lib.data.storage.handlers.MongoStorageHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.discord.BotConfig;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.GuildConfig;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;
import net.buildtheearth.terrabungee.controller.services.StorableStaticInstance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StorageHandler extends Module {
    private static StorageHandler instance = null;

    public static StorageHandler getInstance() {
        if (instance == null) {
            instance = new StorageHandler();
        }
        return instance;
    }

    private StorageHandler() {
        super("storage");
    }

    @Getter
    private final Map<String, Storage> storageMap = Maps.newHashMap();

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
        Storage<ControllerPlayer> playerData = PlayerManager.getInstance().getPlayers();
        playerData.close();
        ((StorageHashMap<?, ?>) playerData).clear();

        Storage<Punishment> punishmentData = PlayerManager.getInstance().getPunishments();
        punishmentData.close();
        ((StorageHashMap<?, ?>) playerData).clear();

        Storage<StorableStaticInstance> staticInstanceData = InstanceManager.getInstance().getStaticInstances();
        staticInstanceData.close();
        ((StorageTreeMap<?, ?>) staticInstanceData).clear();

        Storage<GuildConfig> discordGuildConfigData = DiscordManager.getInstance().getGuildConfigs();
        discordGuildConfigData.close();
        ((StorageHashMap<?, ?>) discordGuildConfigData).clear();

        Storage<BotConfig> discordBotConfigData = DiscordManager.getInstance().getBotConfigs();
        discordBotConfigData.close();
        ((StorageHashMap<?, ?>) discordBotConfigData).clear();

        MongoCredential credential = MongoCredential.createCredential(TerraBungeeConfig.mongodb.user, TerraBungeeConfig.mongodb.database, TerraBungeeConfig.mongodb.password.toCharArray());

        List<ServerAddress> mongoServers = new ArrayList<>();
        for (TerraBungeeConfig.MongoServer mongoServer : TerraBungeeConfig.mongodb.servers) {
            mongoServers.add(new ServerAddress(mongoServer.host, mongoServer.port));
        }

        MongoClient client = new MongoClient(mongoServers, credential, MongoClientOptions.builder().build());
        MongoDatabase database = client.getDatabase(TerraBungeeConfig.mongodb.database);
        {
            MongoStorageHandler<ControllerPlayer> mongoStorageHandler = new MongoStorageHandler<>(database.getCollection("players"));
            mongoStorageHandler.setPriority(100);
            mongoStorageHandler.enableEventUpdates(e -> {
                if (e instanceof MongoCommandException) {
                    TerraBungeeController.logger.warning("Failed to enable event-driven updates for MongoDB. If sledgehammer is being used in a multi-proxy configuration, please set MongoDB up as a replica set.");
                }
            });
            playerData.handlers().register(mongoStorageHandler);
        }

        {
            MongoStorageHandler<Punishment> mongoStorageHandler = new MongoStorageHandler<>(database.getCollection("punishments"));
            mongoStorageHandler.setPriority(100);
            mongoStorageHandler.enableEventUpdates();
            punishmentData.handlers().register(mongoStorageHandler);
        }

        {
            MongoStorageHandler<StorableStaticInstance> mongoStorageHandler = new MongoStorageHandler<>(database.getCollection("static_instances"));
            mongoStorageHandler.setPriority(100);
            mongoStorageHandler.enableEventUpdates();
            staticInstanceData.handlers().register(mongoStorageHandler);
        }

        {
            MongoStorageHandler<GuildConfig> mongoStorageHandler = new MongoStorageHandler<>(database.getCollection("guilds"));
            mongoStorageHandler.setPriority(100);
            mongoStorageHandler.enableEventUpdates();
            discordGuildConfigData.handlers().register(mongoStorageHandler);
        }

        {
            MongoStorageHandler<BotConfig> mongoStorageHandler = new MongoStorageHandler<>(database.getCollection("bots"));
            mongoStorageHandler.setPriority(100);
            mongoStorageHandler.enableEventUpdates();
            discordBotConfigData.handlers().register(mongoStorageHandler);
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
    }

    /**
     * Migrates data from the local storage to the database
     */
    public void migrate() {
        //PlayerManager.getInstance().getPlayers().migrate(0);
        //InstanceManager.getInstance().getStaticInstances().migrate(0);
        //DiscordManager.getInstance().getGuildConfigs().migrate(0);
    }

    @Override
    public void onEnable() {
        File localDb = new File(TerraBungee.getInstance().getFolder(), "local");
        if (!localDb.exists()) {
            localDb.mkdir();
        }
        try {
            Configuration configuration = Configuration.of(TerraBungeeConfig.class);
            configuration.sync(TerraBungeeConfig.class);
        } catch (ClassNotConfigException e) {
            e.printStackTrace();
        }
        loadHandlers();
    }

    @Override
    public void onDisable() {
        storageMap.forEach((a, b) -> {
            try {
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<String> getRequiredModules() {
        return Lists.newArrayList();
    }
}
