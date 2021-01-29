package com.noahhusby.terrabungee.controller;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.controller.config.ConfigHandler;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.discord.DiscordManager;
import com.noahhusby.terrabungee.controller.discord.embeds.ControllerStartedEmbed;
import com.noahhusby.terrabungee.controller.discord.embeds.ControllerStoppedEmbed;
import com.noahhusby.terrabungee.controller.network.NetworkManager;
import com.noahhusby.terrabungee.controller.services.ServiceChecker;
import io.javalin.Javalin;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TerraBungeeController {
    private Javalin webServer;
    public static TerraBungeeConsole logger;

    @Getter private ScheduledExecutorService generalThreads = TerraBungeeUtil.newThreadPoolScheduledExecutor(8, "terrabungee-general");
    @Getter private static TerraBungeeController instance;

    public static boolean isTerraBungeeRunning = true;
    public static boolean isTBQueuedForTermination = false;

    public void enable() {
        instance = this;
        logger = new TerraBungeeConsole();

        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("io.javalin.Javalin")).setLevel(Level.WARN);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.eclipse")).setLevel(Level.WARN);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("net.dv8tion.jda")).setLevel(Level.WARN);

        ConfigHandler.getInstance();
        splash();
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
            if(!isTerraBungeeRunning && !isTBQueuedForTermination) {
                isTBQueuedForTermination = true;
                DiscordManager.getInstance().send(new ControllerStoppedEmbed());
            } else if(isTBQueuedForTermination) {
                System.exit(0);
            }
        }, 0, 5, TimeUnit.SECONDS);


        logger.info("TerraBungee Controller Started!");
        logger.start();
    }

    public void splash() {
        System.out.println("  _____                 ___                        \n" +
                " |_   _|__ _ _ _ _ __ _| _ )_  _ _ _  __ _ ___ ___ \n" +
                "   | |/ -_) '_| '_/ _` | _ \\ || | ' \\/ _` / -_) -_)\n" +
                "   |_|\\___|_| |_| \\__,_|___/\\_,_|_||_\\__, \\___\\___|\n" +
                "                                     |___/         ");
        System.out.println("---------------------------------------------");
        System.out.println("TerraBungee v" + Constants.version + " by Noah Husby");
        System.out.println("Listening on: " + ConfigHandler.host + ":" + ConfigHandler.port);
        System.out.println("---------------------------------------------");
    }
}
