package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

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
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> attributes = TerraBungeeUtil.GSON.fromJson(data.get("attributes"), type);
            PlayerManager.getInstance().updateAttribute(uuid, attributes);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
