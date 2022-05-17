/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * TerraBungeePacketManager.java
 */

/*
 * TerraBungee - Bungeecord Proxy
 * packets/TerraBungeePacketManager.java
 *
 * Author: Noah Husby
 */

package net.buildtheearth.terrabungee.controller.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.noahhusby.lib.data.JsonUtils;
import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.INetworkManager;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SResponsePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CAddStaticInstancePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CHandshakePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CRemoveStaticInstancePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CRetrieveUncachedPlayerPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CServiceMessagePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CSetServiceStatusPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CUpdateAttributeID;
import net.buildtheearth.terrabungee.controller.network.S2C.punishments.S2CBanPlayerPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.punishments.S2CEditPunishmentAction;
import net.buildtheearth.terrabungee.controller.network.S2C.punishments.S2CKickPlayerPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.punishments.S2CMutePlayerPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.punishments.S2CRetrieveActiveBanPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.punishments.S2CRetrievePunishmentPacket;
import net.buildtheearth.terrabungee.controller.network.S2C.punishments.S2CRetrievePunishmentsPacket;
import net.buildtheearth.terrabungee.controller.network.proxy.P2CUpdatePlayersPacket;
import net.buildtheearth.terrabungee.controller.storage.TerraBungeeConfig;
import org.java_websocket.WebSocket;

import java.util.List;
import java.util.Map;

public class NetworkManager extends Module implements INetworkManager {
    private static NetworkManager instance;
    @Getter
    private final Map<String, IS2CPacket> packets = Maps.newHashMap();
    private WSServer server;

    private NetworkManager() {
        super("network");
    }

    public static NetworkManager getInstance() {
        return instance == null ? instance = new NetworkManager() : instance;
    }

    public void onIncomingPayload(WebSocket client, String p) {
        JsonObject payload = JsonUtils.parseString(p).getAsJsonObject();
        String salt = payload.get("salt").getAsString();
        String id = payload.get("id").getAsString();
        String type = payload.get("type").getAsString();

        ServicePacket sp = new ServicePacket(client, id);
        JsonObject data = payload.get("data").getAsJsonObject();

        IS2CPacket packet = packets.get(type);
        if (packet == null) {
            packet = TerraBungee.getInstance().getPluginManager().getPacketMap().get(type);
        }

        if (packet != null) {
            Response response = new Response(sp, salt);
            packet.onMessage(sp, data, response);
            send(new C2SResponsePacket(response));
        }
    }

    @Override
    public void send(IC2SPacket packet) {
        ServicePacket servicePacket = packet.getServicePacket();
        if (servicePacket == null || !servicePacket.getClient().isOpen()) {
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("type", packet.getID());
        payload.addProperty("id", servicePacket.getId());

        JsonObject packetData = new JsonObject();
        packet.getMessage(packetData);
        payload.add("data", packetData);

        servicePacket.getClient().send(TerraBungeeUtil.GSON.toJson(payload));
    }

    public void register(IS2CPacket packet) {
        packets.put(packet.getID(), packet);
    }

    @Override
    public void onEnable() {
        register(new S2CHandshakePacket());
        register(new S2CAddStaticInstancePacket());
        register(new S2CRemoveStaticInstancePacket());
        register(new S2CServiceMessagePacket());
        register(new S2CSetServiceStatusPacket());
        register(new P2CUpdatePlayersPacket());
        register(new S2CRetrieveUncachedPlayerPacket());
        register(new S2CUpdateAttributeID());
        register(new S2CRetrieveActiveBanPacket());
        register(new S2CBanPlayerPacket());
        register(new S2CRetrievePunishmentsPacket());
        register(new S2CRetrievePunishmentPacket());
        register(new S2CKickPlayerPacket());
        register(new S2CEditPunishmentAction());
        register(new S2CMutePlayerPacket());
        server = new WSServer(TerraBungeeConfig.getSocketAddress());
        //TODO: Proper Threading
        new Thread(server).start();
    }

    @Override
    public void onDisable() {
        packets.clear();
        try {
            server.stop();
        } catch (InterruptedException e) {
            getLogger().error("Failed to stop server.", e);
        }
    }

    @Override
    public List<String> getRequiredModules() {
        return Lists.newArrayList("storage", "security");
    }
}