/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - Reponse.java
 */

package net.buildtheearth.terrabungee.common.network;

import com.google.gson.JsonObject;
import lombok.Getter;

public class Response {
    @Getter
    private final JsonObject data;
    @Getter
    private final ResponseCode code;

    public Response(ResponseCode code, JsonObject data) {
        this.code = code;
        this.data = data;
    }

    public enum ResponseCode {
        TIMED_OUT, ERROR, SUCCESS
    }
}
