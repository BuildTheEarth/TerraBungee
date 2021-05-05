package net.buildtheearth.terrabungee.controller;

import ch.qos.logback.classic.Level;
import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.network.INetworkManager;
import net.buildtheearth.api.plugin.PluginManager;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.ControllerStartedEmbed;
import net.buildtheearth.terrabungee.controller.discord.embeds.ControllerStoppedEmbed;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.network.WSServer;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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

        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("io.javalin.Javalin")).setLevel(Level.WARN);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.eclipse")).setLevel(Level.WARN);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("net.dv8tion.jda")).setLevel(Level.WARN);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari.HikariConfig")).setLevel(Level.INFO);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.zaxxer.hikari.pool.HikariPool")).setLevel(Level.INFO);

        folder = new File(System.getProperty("user.dir"));
        folder.mkdir();

        pluginManager = new PluginManager(this);

        pluginFolder = new File(folder, "plugins");
        pluginFolder.mkdir();

        ConfigHandler.getInstance();
        splash();
        pluginManager.detectPlugins(pluginFolder);
        pluginManager.loadPlugins();
        pluginManager.enablePlugins();
        ServiceManager.getInstance();
        DiscordManager.getInstance();

        generalThreads.schedule(() -> DiscordManager.getInstance().send(new ControllerStartedEmbed()), 2, TimeUnit.SECONDS);

        server = new WSServer(new InetSocketAddress(ConfigHandler.host, ConfigHandler.port));
        new Thread(server).start();

        logger.info("TerraBungee Controller Started!");
        logger.start();
    }

    @Override
    public void end() {
        running = false;
        getLogger().info("Shutting down the controller!");
        DiscordManager.getInstance().send(new ControllerStoppedEmbed());
        try {
            server.stop();
        } catch (IOException | InterruptedException ignored) {
        }
        generalThreads.shutdownNow();
        Executors.newSingleThreadScheduledExecutor().schedule(() -> System.exit(0), 1, TimeUnit.SECONDS);
    }

    @Override
    public String getVersion() {
        return Constants.VERSION;
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
    public TBPlayer getPlayer(UUID uuid) {
        return PlayerManager.getInstance().getPlayers().get(uuid);
    }

    @Override
    public TBPlayer getPlayer(String username) {
        return null;
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

}
