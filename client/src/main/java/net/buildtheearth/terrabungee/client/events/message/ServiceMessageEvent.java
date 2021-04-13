/*
 * Copyright (c) 2020 Noah Husby
 * TerraBungeeAPI - ServiceMessageEvent.java
 */

package net.buildtheearth.terrabungee.client.events.message;

import com.google.gson.JsonObject;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.events.Event;

public class ServiceMessageEvent extends Event {

    private final JsonObject message;
    private final String senderID;

    public ServiceMessageEvent(TerraBungeeClient tb, String senderID, JsonObject message) {
        super(tb);
        this.message = message;
        this.senderID = senderID;
    }

    public JsonObject getMessage() {
        return message;
    }

    public String getSenderID() {
        return senderID;
    }
}
