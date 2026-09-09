/*
 * Copyright (c) 2025 BuildTheEarth
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

    /**
     * Registers a server with the given instance id and address to the proxy. If
     * the server already exists, the request is ignored.
     *
     * @param instanceId the id of the instance to add
     * @param address    the address of the instance to add
     */
    public static void addServer(String instanceId, String address) {
        if (TerraBungeeProxy.getServer().getServer(instanceId).isPresent()) {
            TerraBungeeProxy.logger.info("Instance {} tried to be created but already existed?! Ignoring request.", instanceId);
            return;
        }

        InetSocketAddress inetSocketAddress = TerraBungeeUtil.makeInetSocketAddress(address);

        ServerInfo toRegister = new ServerInfo(instanceId, inetSocketAddress);

        TerraBungeeProxy.getServer().registerServer(toRegister);

        TerraBungeeProxy.logger.info("Added instance {} on address {}", instanceId, address);
    }

    /**
     * Unregisters a server with the given instance id from the proxy. If
     * the server does not exist, the request is ignored.
     *
     * @param instanceId the id of the instance to remove
     */
    public static void removeServer(String instanceId) {
        Optional<RegisteredServer> perhapsRegisteredServer = TerraBungeeProxy.getServer().getServer(instanceId);

        if (!perhapsRegisteredServer.isPresent()) {
            TerraBungeeProxy.logger.info("Instance {} tried to be deleted but didn't exist?! Ignoring request.", instanceId);
            return;
        }

        TerraBungeeProxy.getServer().unregisterServer(perhapsRegisteredServer.get().getServerInfo());

        TerraBungeeProxy.logger.info("Removed instance " + instanceId);
    }

    /**
     * Copies all strings in the given iterable that start with the given
     * {@code token} (case insensitive) to the given collection.
     *
     * @param token      the string prefix to search for
     * @param originals  the strings to search through
     * @param collection the collection to add strings to
     * @return the collection with the added strings
     * @throws UnsupportedOperationException if the given collection does not support the add operation
     * @throws IllegalArgumentException       if the given collection is null
     */
    public static <T extends Collection<? super String>> T copyPartialMatches(final String token, final Iterable<String> originals, final T collection) throws UnsupportedOperationException, IllegalArgumentException {
        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    /**
     * Returns true if the given string starts with the given prefix,
     * ignoring the case of both strings.
     *
     * @param string the string to check
     * @param prefix the prefix to check against
     * @return true if the string starts with the prefix (ignoring case)
     * @throws NullPointerException if either parameter is null
     * @throws IllegalArgumentException if the string is shorter than the prefix
     */
    public static boolean startsWithIgnoreCase(final String string, final String prefix) throws IllegalArgumentException, NullPointerException {
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * Retrieves a map of all registered servers on the proxy.
     * The map's keys are the server names, and the values are the corresponding
     * RegisteredServer instances.
     *
     * @return a map of registered server names to their RegisteredServer objects.
     */
    public static Map<String, RegisteredServer> getRegisteredServers() {
        Map<String, RegisteredServer> servers = new HashMap<>();

        TerraBungeeProxy.getServer().getAllServers().forEach((value) -> {
            servers.put(value.getServerInfo().getName(), value);
        });

        return servers;
    }
}
