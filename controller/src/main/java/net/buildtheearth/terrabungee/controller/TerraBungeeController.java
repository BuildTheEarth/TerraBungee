package net.buildtheearth.terrabungee.controller;

import ch.qos.logback.classic.Level;
import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.network.INetworkManager;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.api.plugin.PluginManager;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.command.CommandManager;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.ControllerStartedEmbed;
import net.buildtheearth.terrabungee.controller.discord.embeds.ControllerStoppedEmbed;
import net.buildtheearth.terrabungee.controller.modules.ModuleHandler;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.network.WSServer;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.buildtheearth.terrabungee.controller.security.SecurityManager;
import net.buildtheearth.terrabungee.controller.services.InstanceManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;
import net.buildtheearth.terrabungee.controller.util.LoggerContextUtil;
import net.buildtheearth.terrabungee.controller.util.MySQL;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TerraBungeeController extends TerraBungee {
    public static TerraBungeeConsole logger;

    @Getter
    private File folder;

    @Getter
    private File pluginFolder;

    @Getter
    private PluginManager pluginManager;

    @Getter
    private final ScheduledExecutorService generalThreads = TerraBungeeUtil.newThreadPoolScheduledExecutor(32, "terrabungee-general");
    @Getter
    private static TerraBungeeController instance;

    @Getter
    private boolean running = true;

    private WSServer server;

    protected TerraBungeeController() {
    }

    @Override
    protected void start() {
        instance = this;
        logger = new TerraBungeeConsole();

        // Configure Loggers
        LoggerContextUtil.setLevel("io.javalin.Javalin", Level.WARN);
        LoggerContextUtil.setLevel("org.eclipse", Level.WARN);
        LoggerContextUtil.setLevel("net.dv8tion.jda", Level.WARN);
        configureDatabaseLogging();

        folder = new File(System.getProperty("user.dir"));
        folder.mkdir();

        pluginFolder = new File(folder, "plugins");
        pluginFolder.mkdir();

        pluginManager = new PluginManager(this);

        ConfigHandler.getInstance();
        splash();
        pluginManager.detectPlugins(pluginFolder);
        pluginManager.loadPlugins();
        pluginManager.enablePlugins();
        configureDatabaseLogging();

        ModuleHandler.getInstance().registerModules(SecurityManager.getInstance(), InstanceManager.getInstance(), ServiceManager.getInstance(), PlayerManager.getInstance(), NetworkManager.getInstance(), DiscordManager.getInstance(), CommandManager.getInstance());
        ModuleHandler.getInstance().enableAll();

        server = new WSServer(new InetSocketAddress(ConfigHandler.host, ConfigHandler.port));
        //server.setReuseAddr(true);
        new Thread(server).start();

        generalThreads.schedule(() -> DiscordManager.getInstance().send(new ControllerStartedEmbed()), 2, TimeUnit.SECONDS);

        //TODO Temporary SQL Class until I (MineFact) understand how the Database handling works here
        MySQL.start();

        logger.info("TerraBungee Controller Started!");
        logger.start();


    }

    @Override
    public void end() {
        running = false;
        getLogger().info("Shutting down the controller!");
        ModuleHandler.getInstance().disableAll();
        ConfigHandler.getInstance().closeSqlPool();
        try {
            server.stop();
        } catch (InterruptedException ignored) {
        }
        generalThreads.shutdownNow();
        Executors.newSingleThreadScheduledExecutor().schedule(() -> System.exit(0), 1, TimeUnit.SECONDS);
    }

    @Override
    public String getVersion() {
        return Constants.VERSION.toString();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public INetworkManager getNetworkManager() {
        return NetworkManager.getInstance();
    }

    @Override
    public ControllerPlayer getPlayer(UUID uuid) {
        return PlayerManager.getInstance().getPlayers().get(uuid);
    }

    @Override
    public ControllerPlayer getPlayer(String username) {
        for (ControllerPlayer player : PlayerManager.getInstance().getPlayers().values()) {
            if (player.getName() != null && player.getName().equalsIgnoreCase(username)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public ControllerPlayer getPlayerByDiscordId(String discordId) {
        return PlayerManager.getInstance().getPlayerByDiscordId(discordId);
    }

    @Override
    public ControllerPlayer linkDiscordAccount(UUID uuid, String discordId) {
        synchronized (PlayerManager.getInstance().getPlayers()) {
            ControllerPlayer target = PlayerManager.getInstance().getPlayers().get(uuid);
            if (target == null) {
                return null;
            }
            if (discordId != null) {
                for (ControllerPlayer player : PlayerManager.getInstance().getPlayers().values()) {
                    if (!player.getUniqueID().equals(uuid) && discordId.equals(player.getDiscordId())) {
                        player.setDiscordId(null);
                    }
                }
            }
            target.setDiscordId(discordId);
            PlayerManager.getInstance().getPlayers().saveAsync();
            return target;
        }
    }

    public void splash() {
        System.out.println("  _____                 ___                        \n" +
                           " |_   _|__ _ _ _ _ __ _| _ )_  _ _ _  __ _ ___ ___ \n" +
                           "   | |/ -_) '_| '_/ _` | _ \\ || | ' \\/ _` / -_) -_)\n" +
                           "   |_|\\___|_| |_| \\__,_|___/\\_,_|_||_\\__, \\___\\___|\n" +
                           "                                     |___/         ");
        System.out.println("---------------------------------------------");
        System.out.println("TerraBungee " + Constants.VERSION + " by Noah Husby");
        System.out.println("Listening on: " + ConfigHandler.host + ":" + ConfigHandler.port);
        System.out.println("---------------------------------------------");
    }

    private void configureDatabaseLogging() {
        LoggerContextUtil.setLevel("com.zaxxer.hikari.HikariConfig", Level.WARN);
        LoggerContextUtil.setLevel("com.zaxxer.hikari.pool.HikariPool", Level.WARN);
        LoggerContextUtil.setLevel("com.zaxxer.hikari.util.DriverDataSource", Level.WARN);
    }

}
