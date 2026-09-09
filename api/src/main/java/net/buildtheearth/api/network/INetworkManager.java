package net.buildtheearth.api.network;

import com.google.gson.JsonObject;

/**
 * @author Noah Husby
 */
public interface INetworkManager {
    void send(IC2SPacket packet);

    /**
     * Sends a service message originating from a controller plugin.
     *
     * @param from logical sender id exposed to the receiving service
     * @param to target TerraBungee service id
     * @param message service message payload
     * @return {@code true} when the payload was handed to an open target socket
     */
    boolean sendServiceMessage(String from, String to, JsonObject message);
}
