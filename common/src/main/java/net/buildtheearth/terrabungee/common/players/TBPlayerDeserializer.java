/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeAPI - TBPlayerDeserializer.java
 */

package net.buildtheearth.terrabungee.common.players;

import com.google.gson.*;
import net.buildtheearth.terrabungee.common.util.EventHashMap;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Simple deserializer which replaces the default {@link LinkedList} with an {@link EventHashMap}
 *
 * @author Noah Husby
 */
public class TBPlayerDeserializer implements JsonDeserializer<TBPlayer> {

    private final Gson gson = new Gson();

    @Override
    public TBPlayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TBPlayer player = gson.fromJson(json, TBPlayer.class);
        player.attributes = new EventHashMap<>(player.getAttributes());
        return player;
    }
}
