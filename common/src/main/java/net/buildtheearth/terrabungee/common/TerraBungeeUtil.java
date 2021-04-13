/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - TerraBungeeUtil.java
 */

package net.buildtheearth.terrabungee.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import net.buildtheearth.terrabungee.common.players.TBPlayer;
import net.buildtheearth.terrabungee.common.players.TBPlayerDeserializer;
import net.buildtheearth.terrabungee.common.services.ServiceIntent;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@UtilityClass
public class TerraBungeeUtil {

    public static Gson GSON;

    static {
        GSON = new GsonBuilder().registerTypeAdapter(TBPlayer.class, new TBPlayerDeserializer()).create();
    }

    /**
     * Converts list of intents to a JsonArray
     *
     * @param intents List of active intents
     * @return JSONArray of active intents
     */
    public static JsonArray intentsToArray(List<ServiceIntent> intents) {
        JsonArray a = new JsonArray();
        for (ServiceIntent intent : intents) {
            a.add(intent.name());
        }
        return a;
    }

    /**
     * Converts JSONArray to a list of intents
     *
     * @param a JSONArray of active intents
     * @return List of active intents
     */
    public static List<ServiceIntent> arrayToIntents(JsonArray a) {
        List<ServiceIntent> intents = new ArrayList<>();
        for (JsonElement o : a) {
            intents.add(ServiceIntent.valueOf(o.getAsString()));
        }
        return intents;
    }

    /**
     * Creates {@link InetSocketAddress} from {@link String}
     *
     * @param address
     * @return {@link InetSocketAddress}
     */
    public static InetSocketAddress makeInetSocketAddress(String address) {
        try {
            URI uri = new URI("abc://" + address);
            String host = uri.getHost();
            int port = uri.getPort();
            if (uri.getHost() == null || uri.getPort() == -1) {
                return null;
            }
            return new InetSocketAddress(host, port);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static JsonObject parse(String s) {
        return new JsonParser().parse(s).getAsJsonObject();
    }

    /**
     * Generate a random salt code
     *
     * @return Salt code
     */
    public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static ExecutorService newSingleThreadExecutor(String name) {
        return Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(name + "-%d").build());
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String name) {
        return Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(name + "-%d").build());
    }

    public static ExecutorService newThreadPoolExecutor(int threads, String name) {
        return Executors.newFixedThreadPool(threads, new ThreadFactoryBuilder().setNameFormat(name + "-%d").build());
    }

    public static ScheduledExecutorService newThreadPoolScheduledExecutor(int threads, String name) {
        return Executors.newScheduledThreadPool(threads, new ThreadFactoryBuilder().setNameFormat(name + "-%d").build());
    }
}
