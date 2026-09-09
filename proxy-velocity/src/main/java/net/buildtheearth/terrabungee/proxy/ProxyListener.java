package net.buildtheearth.terrabungee.proxy;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.EventTask;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Noah Husby & XboxBedrock
 */
public class ProxyListener {
    private static final long BAN_TIMEOUT_SECONDS = 2;
    private static final long WARNING_INTERVAL_NANOS = TimeUnit.MINUTES.toNanos(1);
    private static final AtomicLong LAST_BAN_LOOKUP_WARNING = new AtomicLong();

    @Subscribe(order = PostOrder.LATE)
    public EventTask onLoginEvent(PlayerChooseInitialServerEvent e) {
        if(!TerraBungeeProxy.getInstance().getTerraBungee().getNetworkManager().isConnectionEstablished())
            return EventTask.resumeWhenComplete(CompletableFuture.completedFuture(null));

        try {
            CompletableFuture<Response> request = TerraBungeeProxy.getInstance().getTerraBungee()
                    .getNetworkManager().send(new S2CRetrieveActiveBanPacket(e.getPlayer().getUniqueId()));
            CompletableFuture<BanCheckResult> check = resolveBanCheck(request, BAN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            CompletableFuture<Void> completion = check.thenAccept(result -> {
                if (result == BanCheckResult.ERROR) {
                    warnBanLookupFailure();
                    return;
                }
                if (result != BanCheckResult.BANNED) {
                    return;
                }
                Response response = request.getNow(null);
                if (response == null) {
                    warnBanLookupFailure();
                    return;
                }
                try {
                    Punishment punishment = TerraBungeeUtil.GSON.fromJson(response.getData(), Punishment.class);
                    e.getPlayer().disconnect(PlayerHandler.getInstance().getBanDisconnectMessage(punishment));
                } catch (RuntimeException exception) {
                    TerraBungeeProxy.getLogger().warn("Could not decode an active ban response; allowing login.", exception);
                }
            });
            return EventTask.resumeWhenComplete(completion);
        } catch (RuntimeException exception) {
            warnBanLookupFailure();
            return EventTask.resumeWhenComplete(CompletableFuture.completedFuture(null));
        }
    }

    static CompletableFuture<BanCheckResult> resolveBanCheck(
            CompletableFuture<Response> responseFuture, long timeout, TimeUnit unit) {
        return responseFuture.orTimeout(timeout, unit).handle((response, throwable) -> {
            if (throwable != null || response == null) {
                return BanCheckResult.ERROR;
            }
            return response.getCode() == Response.ResponseCode.SUCCESS
                    ? BanCheckResult.BANNED
                    : BanCheckResult.ALLOWED;
        });
    }

    private static void warnBanLookupFailure() {
        long now = System.nanoTime();
        long previous = LAST_BAN_LOOKUP_WARNING.get();
        if (now - previous >= WARNING_INTERVAL_NANOS
                && LAST_BAN_LOOKUP_WARNING.compareAndSet(previous, now)) {
            TerraBungeeProxy.getLogger().warn(
                    "TerraBungee ban lookup failed or timed out after two seconds; allowing login (fail-open)."
            );
        }
    }

    enum BanCheckResult {
        BANNED,
        ALLOWED,
        ERROR
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
