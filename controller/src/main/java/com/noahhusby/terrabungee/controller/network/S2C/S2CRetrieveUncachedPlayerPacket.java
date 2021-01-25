package com.noahhusby.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.controller.console.ConsoleColor;
import com.noahhusby.terrabungee.controller.console.TerraBungeeConsole;
import com.noahhusby.terrabungee.controller.console.TextComponent;
import com.noahhusby.terrabungee.controller.network.IS2CPacket;
import com.noahhusby.terrabungee.controller.network.Response;
import com.noahhusby.terrabungee.controller.network.ServicePacket;

import java.util.UUID;

public class S2CRetrieveUncachedPlayerPacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.retrieveUncachedPlayerID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        String name = data.get("name").getAsString();
        try {
            UUID uuid =  UUID.fromString(name);
            //TODO: Check for player
        } catch (IllegalArgumentException e) {

        }
    }
}
