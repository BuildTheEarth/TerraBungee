package net.buildtheearth.terrabungee.proxy.network;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.proxy.players.PlayerHandler;
import net.buildtheearth.terrabungee.client.TerraBungeeClient;
import net.buildtheearth.terrabungee.client.network.IC2SPacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Noah Husby
 */
public class C2PMuteCachePacket implements IC2SPacket {
    @Override
    public String getType() {
        return "mute_cache";
    }

    @Override
    public void onMessage(TerraBungeeClient instance, JsonObject data) {
        List<Punishment> mutes = TerraBungeeUtil.GSON.fromJson(data.get("mutes"), new TypeToken<List<Punishment>>() {}.getType());
        Map<UUID, Punishment> activeMutes = Maps.newHashMap();

        if(mutes == null)
            return;

        for (Punishment mute : mutes) {
            activeMutes.put(mute.getPlayer(), mute);
        }
        PlayerHandler.getInstance().setMuteCache(ImmutableMap.copyOf(activeMutes));
    }
}
