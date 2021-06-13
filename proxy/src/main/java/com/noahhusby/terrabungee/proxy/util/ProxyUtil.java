/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - ProxyHelper.java
 */

package com.noahhusby.terrabungee.proxy.util;

import com.noahhusby.terrabungee.proxy.TerraBungeeProxy;
import lombok.experimental.UtilityClass;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.TimeZone;

/**
 * @author Noah Husby
 */
@UtilityClass
public class ProxyUtil {

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a yyyy/MM/dd");

    public static String toReadableTime(LocalDateTime date) {
        return date.format(timeFormat);
    }

    public static void addServer(String instanceId, String address) {
        if (ProxyServer.getInstance().getServerInfo(instanceId) != null) {
            TerraBungeeProxy.LOGGER.info("Instance " + instanceId + " tried to be created but already existed?! Ignoring request.");
            return;
        }

        InetSocketAddress inetSocketAddress = TerraBungeeUtil.makeInetSocketAddress(address);
        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(
                instanceId, inetSocketAddress,
                "A TerraBungee Instance - " + instanceId, false
        );

        ProxyServer.getInstance().getServers().put(instanceId, serverInfo);
        TerraBungeeProxy.LOGGER.info("Added instance " + instanceId + " on address " + address);
    }

    public static void removeServer(String instanceId) {
        if (ProxyServer.getInstance().getServerInfo(instanceId) == null) {
            TerraBungeeProxy.LOGGER.info("Instance " + instanceId + " tried to be deleted but didn't exist?! Ignoring request.");
            return;
        }

        ProxyServer.getInstance().getServers().remove(instanceId);
        TerraBungeeProxy.LOGGER.info("Removed instance " + instanceId);
    }
}
