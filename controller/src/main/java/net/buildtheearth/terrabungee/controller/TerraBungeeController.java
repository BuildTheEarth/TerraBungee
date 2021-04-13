package net.buildtheearth.terrabungee.controller;

import ch.qos.logback.classic.Level;
import io.javalin.Javalin;
import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.plugin.PluginManager;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;
import net.buildtheearth.terrabungee.controller.console.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.ControllerStartedEmbed;
import net.buildtheearth.terrabungee.controller.discord.embeds.ControllerStoppedEmbed;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class TerraBungeeController extends TerraBungee {
    private Javalin webServer;
    public static TerraBungeeConsole logger;

    @Getter
    private PluginManager pluginManager;

    @Getter
    private ScheduledExecutorService generalThreads = TerraBungeeUtil.newThreadPoolScheduledExecutor(32, "terrabungee-general");
    @Getter
    private static TerraBungeeController instance;

    public static boolean isTerraBungeeRunning = true;
    public static boolean isTBQueuedForTermination = false;

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

        pluginManager = new PluginManager(this);

        File f = new File(System.getProperty("user.dir"), "plugins");
        f.mkdir();

        ConfigHandler.getInstance();
        splash();
        pluginManager.detectPlugins(f);
        pluginManager.loadPlugins();
        pluginManager.enablePlugins();
        ServiceManager.getInstance();
        DiscordManager.getInstance();

        generalThreads.schedule(() -> DiscordManager.getInstance().send(new ControllerStartedEmbed()), 2, TimeUnit.SECONDS);

        webServer = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.logIfServerNotStarted = false;
        }).start(ConfigHandler.host, ConfigHandler.port);

        generalThreads.schedule(() -> {
            webServer.ws("/", wsHandler -> wsHandler.onMessage(ctx -> NetworkManager.getInstance().onIncomingPayload(ctx, ctx.message())));
            logger.info("Starting WebSocket Server!");
        }, 2, TimeUnit.SECONDS);

        generalThreads.scheduleAtFixedRate(() -> {
            if (!isTerraBungeeRunning && !isTBQueuedForTermination) {
                isTBQueuedForTermination = true;
                DiscordManager.getInstance().send(new ControllerStoppedEmbed());
            } else if (isTBQueuedForTermination) {
                System.exit(0);
            }
        }, 0, 5, TimeUnit.SECONDS);


        logger.info("TerraBungee Controller Started!");
        logger.start();
    }

    @Override
    public String getVersion() {
        return Constants.VERSION;
    }

    @Override
    public Logger getLogger() {
        return logger;
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
