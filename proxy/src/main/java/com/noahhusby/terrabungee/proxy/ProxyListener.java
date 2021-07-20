package com.noahhusby.terrabungee.proxy;

import com.noahhusby.terrabungee.proxy.players.PlayerHandler;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrieveActiveBanPacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.ExecutionException;

/**
 * @author Noah Husby
 */
public class ProxyListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoginEvent(LoginEvent e) {
        try {
            Response punishmentResponse = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrieveActiveBanPacket(e.getConnection().getUniqueId())).get();
            if (punishmentResponse.getCode() == Response.ResponseCode.SUCCESS) {
                Punishment punishment = TerraBungeeUtil.GSON.fromJson(punishmentResponse.getData(), Punishment.class);
                e.getConnection().disconnect(PlayerHandler.getInstance().getBanDisconnectMessage(punishment));
            }
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }
}
