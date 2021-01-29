package com.noahhusby.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungeeUtil;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;
import com.noahhusby.terrabungee.controller.players.PlayerManager;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/**
 * @author Noah Husby
 */
public class S2CUpdateAttributeID implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.updateAttributeID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        try {
            UUID uuid = UUID.fromString(data.get("uuid").getAsString());
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> attributes = TerraBungeeUtil.GSON.fromJson(data.get("attributes"), type);
            PlayerManager.getInstance().updateAttribute(uuid, attributes);
        } catch (IllegalArgumentException ignored) { }
    }
}
