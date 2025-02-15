/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - ProxyHelper.java
 */

package net.buildtheearth.terrabungee.proxy.util;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import lombok.experimental.UtilityClass;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        if (TerraBungeeProxy.getServer().getServer(instanceId).isPresent()) {
            TerraBungeeProxy.LOGGER.info("Instance {} tried to be created but already existed?! Ignoring request.", instanceId);
            return;
        }

        InetSocketAddress inetSocketAddress = TerraBungeeUtil.makeInetSocketAddress(address);

        ServerInfo toRegister = new ServerInfo(instanceId, inetSocketAddress);

        TerraBungeeProxy.getServer().registerServer(toRegister);

        TerraBungeeProxy.LOGGER.info("Added instance {} on address {}", instanceId, address);
    }

    public static void removeServer(String instanceId) {
        Optional<RegisteredServer> perhapsRegisteredServer = TerraBungeeProxy.getServer().getServer(instanceId);

        if (!perhapsRegisteredServer.isPresent()) {
            TerraBungeeProxy.LOGGER.info("Instance {} tried to be deleted but didn't exist?! Ignoring request.", instanceId);
            return;
        }

        TerraBungeeProxy.getServer().unregisterServer(perhapsRegisteredServer.get().getServerInfo());

        TerraBungeeProxy.LOGGER.info("Removed instance " + instanceId);
    }

    public static <T extends Collection<? super String>> T copyPartialMatches(final String token, final Iterable<String> originals, final T collection) throws UnsupportedOperationException, IllegalArgumentException {
        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    public static boolean startsWithIgnoreCase(final String string, final String prefix) throws IllegalArgumentException, NullPointerException {
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static Map<String, RegisteredServer> getRegisteredServers() {
        Map<String, RegisteredServer> servers = new HashMap<>();

        TerraBungeeProxy.getServer().getAllServers().forEach((value) -> {
            servers.put(value.getServerInfo().getName(), value);
        });

        return servers;
    }
}
