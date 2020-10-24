package com.noahhusby.terrabungee.api.network.S2C;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.network.IS2CPacket;
import com.noahhusby.terrabungee.api.network.PacketResponse;
import com.noahhusby.terrabungee.api.network.Response;
import org.json.simple.JSONObject;

import java.util.Random;

public class S2CResponsePacket implements IS2CPacket {

    private final IS2CPacket packet;
    private final PacketResponse response;
    private final int timeout;
    private final String salt;

    public S2CResponsePacket(IS2CPacket packet, PacketResponse response) {
        this(packet, response, Constants.responseTimeout);
    }

    public S2CResponsePacket(IS2CPacket packet, PacketResponse response, int timeout) {
        this.packet = packet;
        this.response = response;
        this.timeout = timeout;
        this.salt = getSaltString();
    }

    @Override
    public String getType() {
        return Constants.packetResponseID;
    }

    @Override
    public JSONObject getMessage(TerraBungee instance, JSONObject data) {
        instance.getNetworkManager().addResponse(new Response(response, salt, System.currentTimeMillis(), timeout));
        JSONObject payload = new JSONObject();
        payload.put("type", packet.getType());
        payload.put("id", instance.getId());
        payload.put("data", packet.getMessage(instance, new JSONObject()));

        data.put("packet", payload.toJSONString());
        data.put("salt", salt);
        return data;
    }

    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

}
