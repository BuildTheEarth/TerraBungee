

/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - InstanceManager.java
 */

package net.buildtheearth.terrabungee.client;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.terrabungee.client.network.S2C.S2CAddStaticInstancePacket;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRemoveStaticInstancePacket;
import net.buildtheearth.terrabungee.client.util.Manager;
import net.buildtheearth.terrabungee.common.instance.Instance;
import net.buildtheearth.terrabungee.common.network.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InstanceManager extends Manager {

    @Getter
    @Setter
    private List<Instance> instances = new ArrayList<>();

    protected InstanceManager(TerraBungeeClient tb) {
        super(tb);
    }

    /**
     * Add a new static instance
     *
     * @param id      ID of static instance
     * @param address IP Address
     * @return {@link Response}
     */
    public CompletableFuture<Response> addStaticInstance(String id, String address) {
        return tb.getNetworkManager().send(new S2CAddStaticInstancePacket(id, address));
    }

    /**
     * Remove a static instance
     *
     * @param id ID of static instance
     * @return {@link Response}
     */
    public CompletableFuture<Response> removeStaticInstance(String id) {
        return tb.getNetworkManager().send(new S2CRemoveStaticInstancePacket(id));
    }
}
