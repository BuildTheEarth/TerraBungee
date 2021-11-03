package net.buildtheearth.terrabungee.controller.network.proxy;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Noah Husby
 */
@AllArgsConstructor
public class C2PMuteCachePacket implements IC2SPacket {

    private final TerraBungeeService proxy;

    @Override
    public String getID() {
        return "mute_cache";
    }

    @Override
    public void getMessage(JsonObject data) {
        List<Punishment> activeMutes = Lists.newArrayList();
        for (Punishment punishment : PlayerManager.getInstance().getPunishments().values()) {
            if (punishment.getType() == Punishment.Type.MUTE && punishment.isActive()) {
                activeMutes.add(punishment);
            }
        }
        if(activeMutes.size() == 0)
            return;

        data.add("mutes", TerraBungeeUtil.GSON.toJsonTree(activeMutes));
    }

    @Override
    public ServicePacket getServicePacket() {
        return ServicePacket.fromService(proxy);
    }
}
