package net.buildtheearth.api.network;

/**
 * @author Noah Husby
 */
public interface INetworkManager {
    void send(IC2SPacket packet);
}
