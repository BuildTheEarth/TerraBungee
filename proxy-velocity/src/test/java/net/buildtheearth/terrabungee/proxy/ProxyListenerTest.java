package net.buildtheearth.terrabungee.proxy;

import net.buildtheearth.terrabungee.common.network.Response;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProxyListenerTest {

    @Test
    void successfulBanLookupBlocksLogin() {
        CompletableFuture<Response> response = CompletableFuture.completedFuture(
                new Response(Response.ResponseCode.SUCCESS, null));

        assertEquals(ProxyListener.BanCheckResult.BANNED,
                ProxyListener.resolveBanCheck(response, 1, TimeUnit.SECONDS).join());
    }

    @Test
    void nonBanResponseAllowsLogin() {
        CompletableFuture<Response> response = CompletableFuture.completedFuture(
                new Response(Response.ResponseCode.ERROR, null));

        assertEquals(ProxyListener.BanCheckResult.ALLOWED,
                ProxyListener.resolveBanCheck(response, 1, TimeUnit.SECONDS).join());
    }

    @Test
    void delayedLookupFailsOpenAfterTimeout() {
        CompletableFuture<Response> response = new CompletableFuture<>();

        assertEquals(ProxyListener.BanCheckResult.ERROR,
                ProxyListener.resolveBanCheck(response, 25, TimeUnit.MILLISECONDS).join());
    }

    @Test
    void failedLookupFailsOpen() {
        CompletableFuture<Response> response = CompletableFuture.failedFuture(
                new IllegalStateException("controller unavailable"));

        assertEquals(ProxyListener.BanCheckResult.ERROR,
                ProxyListener.resolveBanCheck(response, 1, TimeUnit.SECONDS).join());
    }
}
