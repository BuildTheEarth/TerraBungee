/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - TerraBungeeProxyMain.java
 */

package net.buildtheearth.terrabungee.proxy;


import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.buildtheearth.terrabungee.proxy.commands.FindCommand;
import net.buildtheearth.terrabungee.proxy.commands.GBanCommand;
import net.buildtheearth.terrabungee.proxy.commands.GKickCommand;
import net.buildtheearth.terrabungee.proxy.commands.GMuteCommand;
import net.buildtheearth.terrabungee.proxy.commands.PunishmentCommand;
import net.buildtheearth.terrabungee.proxy.commands.ServerCommand;
import net.buildtheearth.terrabungee.proxy.commands.TerraBungeeAdminCommand;
import net.buildtheearth.terrabungee.proxy.commands.TerraBungeeCommand;
import net.buildtheearth.terrabungee.proxy.config.ConfigHandler;
import net.buildtheearth.terrabungee.proxy.network.C2PMuteCachePacket;
import net.buildtheearth.terrabungee.proxy.network.C2PProxyBanDisconnectPacket;
import net.buildtheearth.terrabungee.proxy.network.C2PProxyKickDisconnectPacket;
import net.buildtheearth.terrabungee.proxy.players.PlayerHandler;
import lombok.Getter;
import net.buildtheearth.terrabungee.client.TerraBungeeAPI;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;
import net.buildtheearth.terrabungee.common.services.ServiceType;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "terrabungeeproxy",
        name = "TerraBungeeProxy",
        description = "TerraBungeeProxy but Velocity",
        version = "1.0.0",
        authors = { "NoahHusby", "MineFact", "XboxBedrock" }
)
public class TerraBungeeProxy {
    @Getter
    private static TerraBungeeProxy instance = null;
    @Getter
    private TerraBungeeClient terraBungee;

    @Getter
    public static Logger logger;

    @Getter
    private static ProxyServer server;

    @Getter
    private static Path dataDirectory;

    @Inject
    public TerraBungeeProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        logger.info("TerraBungeeProxy Constructed");
    }

    @Subscribe
    void onProxyInitialization(final ProxyInitializeEvent event) {
        instance = this;

        server.getEventManager().register(this, new ProxyListener());
        ConfigHandler.getInstance();
        PlayerHandler.getInstance();

        terraBungee = TerraBungeeAPI.createService(ServiceType.PROXY, ConfigHandler.serviceID, ConfigHandler.controllerUrl);
        terraBungee.setAutoReconnect(true);
        terraBungee.connect();
        terraBungee.enableIntents(ServiceIntent.INSTANCE_UPDATE);
        terraBungee.getNetworkManager().register(new C2PProxyBanDisconnectPacket());
        terraBungee.getNetworkManager().register(new C2PProxyKickDisconnectPacket());
        terraBungee.getNetworkManager().register(new C2PMuteCachePacket());
        terraBungee.addListener(new TBListener());

        server.getCommandManager().register(getCommandMeta("find"), new FindCommand());
        server.getCommandManager().register(getCommandMeta("gban"), new GBanCommand());
        server.getCommandManager().register(getCommandMeta("gkick"), new GKickCommand());
        server.getCommandManager().register(getCommandMeta("gmute"), new GMuteCommand());
        server.getCommandManager().register(getCommandMeta("server"), new ServerCommand());
        server.getCommandManager().register(getCommandMeta("punishment"), new PunishmentCommand());
        server.getCommandManager().register(getCommandMeta("tb"), new TerraBungeeCommand());
        server.getCommandManager().register(getCommandMeta("tba"), new TerraBungeeAdminCommand());
    }

    /*
    @EventHandler
    public void onProxyJoin(ServerConnectEvent e) {

		if(e.getReason() == ServerConnectEvent.Reason.JOIN_PROXY && !ConfigHandler.queueServer.equals(""))
			e.getPlayer().connect(ProxyServer.getInstance().getServerInfo(ConfigHandler.queueServer));

    }
    */


    @Subscribe
    void onProxyShutdown(final ProxyShutdownEvent event) {
        server.getScheduler().tasksByPlugin(this).forEach(ScheduledTask::cancel);
        instance = null;
        terraBungee.discard();
    }

    public CommandMeta getCommandMeta(String command) {
        return server.getCommandManager().metaBuilder(command).plugin(this).build();
    }
}
