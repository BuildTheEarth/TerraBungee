/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - Response.java
 */

package net.buildtheearth.terrabungee.client.network;

import lombok.Getter;
import net.buildtheearth.terrabungee.common.network.Response;

import java.util.concurrent.CompletableFuture;

public class ResponseRequest {
    @Getter
    private final CompletableFuture<Response> future;
    @Getter
    private final long time;
    @Getter
    private final long timeout;

    public ResponseRequest(CompletableFuture<Response> future, int timeout) {
        this.future = future;
        this.time = System.currentTimeMillis();
        this.timeout = time + timeout;
    }
}


