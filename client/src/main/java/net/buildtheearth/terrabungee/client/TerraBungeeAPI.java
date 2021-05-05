/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - TerraBungeeAPI.java
 */

package net.buildtheearth.terrabungee.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.buildtheearth.terrabungee.client.network.IS2CPacket;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.services.ServiceType;

import java.util.concurrent.CompletableFuture;

/**
 * @author Noah Husby
 */
@UtilityClass
public class TerraBungeeAPI {
    /**
     * Create a new TerraBungee service
     *
     * @param serviceType {@link ServiceType}
     * @param Id          Unique ID of Service
     * @param controller  IP of Controller
     * @return {@link TerraBungeeClient}
     */
    public static TerraBungeeClient createService(@NonNull ServiceType serviceType, @NonNull String Id, @NonNull String controller) {
        return new TerraBungeeClient(serviceType, Id, controller);
    }

    public static void main(String[] args) {
        TerraBungeeClient client = createService(ServiceType.CUSTOM, "TEST", "144.217.77.29:7500");
        client.connect();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CompletableFuture<Response> response = client.getNetworkManager().send(new IS2CPacket() {
            @Override
            public String getType() {
                return "discord_link_request";
            }

            @Override
            public void getMessage(TerraBungeeClient instance, JsonObject data) {
                data.addProperty("uuid", "4cfa7dc1-3021-42b0-969b-224a9656cc6d");
            }
        });

        response.thenAccept(response1 -> {
           System.out.println(response1.getCode().name());
           System.out.println(new Gson().toJson(response1.getData()));
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client.getNetworkManager().send(new IS2CPacket() {
            @Override
            public String getType() {
                return "discord_message";
            }

            @Override
            public void getMessage(TerraBungeeClient instance, JsonObject data) {
                data.addProperty("uuid", "4cfa7dc1-3021-42b0-969b-224a9656cc6d");
                data.addProperty("message", "Some amazing magical message! **Hello There** :joy:");
            }
        });
    }
}
