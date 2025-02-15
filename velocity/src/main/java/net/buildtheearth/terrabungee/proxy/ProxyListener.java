package net.buildtheearth.terrabungee.proxy;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.buildtheearth.terrabungee.proxy.players.PlayerHandler;
import net.buildtheearth.terrabungee.proxy.util.DateUtil;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import net.buildtheearth.terrabungee.client.network.S2C.S2CRetrieveActiveBanPacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.common.network.Response;
import net.buildtheearth.terrabungee.common.players.Punishment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * @author Noah Husby & XboxBedrock
 */
public class ProxyListener {
    @Subscribe(order = PostOrder.LATE)
    public void onLoginEvent(PlayerChooseInitialServerEvent e) {
        try {
            Response punishmentResponse = TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().send(new S2CRetrieveActiveBanPacket(e.getPlayer().getUniqueId())).get();
            if (punishmentResponse.getCode() == Response.ResponseCode.SUCCESS) {
                Punishment punishment = TerraBungeeUtil.GSON.fromJson(punishmentResponse.getData(), Punishment.class);
                e.getPlayer().disconnect(PlayerHandler.getInstance().getBanDisconnectMessage(punishment));
            }
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onEarlyChat(PlayerChatEvent e) {
        if (e.getResult() != PlayerChatEvent.ChatResult.denied() || e.getMessage().startsWith("/")) {
            return;
        }

        Player player = e.getPlayer();
        Punishment mute = PlayerHandler.getInstance().getMuteCache().get(player.getUniqueId());
        if (mute != null) {
            e.setResult(PlayerChatEvent.ChatResult.denied());

            if (mute.getEnd() == null) {
                player.sendMessage(
                        Component.text()
                                .appendNewline()
                                .appendNewline()
                                .append(Component.text()
                                        .content("You have been muted permanently")
                                        .color(NamedTextColor.RED)
                                    )
                                .append(Component.text()
                                        .content(" for ")
                                        .color(NamedTextColor.GRAY)
                                        )
                                .append(Component.text()
                                        .content(mute.getReason())
                                        .color(NamedTextColor.YELLOW)
                                        )
                );
            } else {
                player.sendMessage(
                        Component.text()
                                .appendNewline()
                                .appendNewline()
                                .append(Component.text()
                                        .content("You have been muted for ")
                                        .color(NamedTextColor.RED)
                                        )
                                .append(Component.text()
                                        .content(DateUtil.getExpandedTimeMessage(LocalDateTime.parse(mute.getEnd()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - new Date().getTime()))
                                        )
                                .append(Component.text()
                                        .content(" for ")
                                        .color(NamedTextColor.GRAY)
                                        )
                                .append(Component.text()
                                        .content(mute.getReason())
                                        .color(NamedTextColor.YELLOW)
                                        )
                );
            }

            player.sendMessage(
                    Component.text()
                            .append(Component.text()
                                    .content("Punishment ID: ")
                                    .color(NamedTextColor.GRAY)
                                    )
                            .append(Component.text()
                                    .content(String.valueOf(mute.getId()))
                                    .color(NamedTextColor.YELLOW)
                            )
                            .appendNewline()
                            .appendNewline()
            );
        }
    }

}
