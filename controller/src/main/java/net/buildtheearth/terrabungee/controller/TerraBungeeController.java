package net.buildtheearth.terrabungee.controller;

import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.network.INetworkManager;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.api.plugin.PluginManager;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.command.CommandManager;
import net.buildtheearth.terrabungee.controller.discord.DiscordManager;
import net.buildtheearth.terrabungee.controller.discord.embeds.ControllerStartedEmbed;
import net.buildtheearth.terrabungee.controller.instance.InstanceManager;
import net.buildtheearth.terrabungee.controller.logging.TerraBungeeConsole;
import net.buildtheearth.terrabungee.controller.modules.ModuleHandler;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.buildtheearth.terrabungee.controller.security.SecurityManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;
import net.buildtheearth.terrabungee.controller.storage.StorageHandler;
import net.buildtheearth.terrabungee.controller.storage.TerraBungeeConfig;
import org.slf4j.Logger;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TerraBungeeController extends TerraBungee {

    public static Logger logger;
    @Getter
    private static TerraBungeeConsole console;
    @Getter
    private static TerraBungeeController instance;
    @Getter
    private final ScheduledExecutorService generalThreads = TerraBungeeUtil.newThreadPoolScheduledExecutor(32, "terrabungee-general");
    @Getter
    private File folder;
    @Getter
    private File pluginFolder;
    @Getter
    private PluginManager pluginManager;
    @Getter
    private boolean running = true;

    protected TerraBungeeController() {
    }

    @Override
    protected void start() {
        instance = this;
        console = new TerraBungeeConsole();
        logger = console.getLogger();


        folder = new File(System.getProperty("user.dir"));
        folder.mkdir();

        pluginFolder = new File(folder, "plugins");
        pluginFolder.mkdir();

        pluginManager = new PluginManager(this);

        splash();
        pluginManager.detectPlugins(pluginFolder);
        pluginManager.loadPlugins();
        pluginManager.enablePlugins();

        ModuleHandler.getInstance().registerModules(StorageHandler.getInstance(), SecurityManager.getInstance(), InstanceManager.getInstance(), ServiceManager.getInstance(), PlayerManager.getInstance(), NetworkManager.getInstance(), DiscordManager.getInstance(), CommandManager.getInstance());
        ModuleHandler.getInstance().enableAll();

        generalThreads.schedule(() -> DiscordManager.getInstance().send(new ControllerStartedEmbed()), 2, TimeUnit.SECONDS);

        logger.info("TerraBungee Controller Started!");
        console.start();
    }

    @Override
    public void end() {
        running = false;
        getLogger().info("Shutting down the controller!");
        StorageHandler.getInstance().unload();
        ModuleHandler.getInstance().disableAll();
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
        System.out.println("Listening on: " + TerraBungeeConfig.host + ":" + TerraBungeeConfig.port);
        System.out.println("---------------------------------------------");
    }

}
