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
import net.buildtheearth.terrabungee.common.services.ServiceStatus;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SResponsePacket;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SServiceMessagePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CAddStaticInstancePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CKeepAlivePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CRemoveStaticInstancePacket;
import net.buildtheearth.terrabungee.controller.network.S2C.S2CRetrieveAllPlayersPacket;
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
import net.buildtheearth.terrabungee.controller.services.ServiceManager;
import org.java_websocket.WebSocket;

import java.util.Map;
import java.util.logging.Level;

public class NetworkManager implements INetworkManager, Module {
    private static NetworkManager instance;

    public static NetworkManager getInstance() {
        return instance == null ? instance = new NetworkManager() : instance;
    }

    @Getter
    private final Map<String, IS2CPacket> packets = Maps.newHashMap();

    public void onIncomingPayload(WebSocket client, String p) {
        Response response = null;
        String type = "unknown";
        try {
            JsonObject payload = JsonUtils.parseString(p).getAsJsonObject();
            String salt = payload.get("salt").getAsString();
            String id = payload.get("id").getAsString();
            type = payload.get("type").getAsString();

            ServicePacket sp = new ServicePacket(client, id);
            JsonObject data = payload.get("data").getAsJsonObject();
            response = new Response(sp, salt);

            IS2CPacket packet = packets.get(type);
            if (packet == null) {
                packet = TerraBungee.getInstance().getPluginManager().getPacketMap().get(type);
            }

            if (packet == null) {
                response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
            } else {
                packet.onMessage(sp, data, response);
            }
        } catch (Exception e) {
            TerraBungeeController.logger.log(Level.WARNING,
                    "Unable to process TerraBungee packet " + type, e);
            if (response != null) {
                response.setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode.ERROR);
            }
        }

        if (response != null) {
            trySend(new C2SResponsePacket(response));
        }
    }

    @Override
    public void send(IC2SPacket packet) {
        trySend(packet);
    }

    @Override
    public boolean sendServiceMessage(String from, String to, JsonObject message) {
        if (from == null || from.isBlank() || to == null || to.isBlank() || message == null) {
            return false;
        }

        TerraBungeeService target = ServiceManager.getInstance().getService(to);
        if (target == null || target.getStatus() != ServiceStatus.ONLINE
                || !(target.getClient() instanceof WebSocket)
                || !((WebSocket) target.getClient()).isOpen()) {
            return false;
        }

        return trySend(new C2SServiceMessagePacket(from, to, message.toString()));
    }

    public boolean trySend(IC2SPacket packet) {
        ServicePacket servicePacket = packet.getServicePacket();
        if (servicePacket == null || servicePacket.getClient() == null || !servicePacket.getClient().isOpen()) {
            return false;
        }

        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("type", packet.getID());
            payload.addProperty("id", servicePacket.getId());

            JsonObject packetData = new JsonObject();
            packet.getMessage(packetData);
            payload.add("data", packetData);

            servicePacket.getClient().send(TerraBungeeUtil.GSON.toJson(payload));
            return true;
        } catch (Exception e) {
            ServiceManager.getInstance().markDisconnected(servicePacket.getClient());
            TerraBungeeController.logger.warning(
                    "Unable to send TerraBungee packet to " + servicePacket.getId() + ": " + e.getMessage());
            return false;
        }
    }

    public void register(IS2CPacket packet) {
        packets.put(packet.getID(), packet);
    }

    @Override
    public void onEnable() {
        register(new S2CKeepAlivePacket());
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
        register(new S2CRetrieveAllPlayersPacket());
    }

    @Override
    public void onDisable() {
        packets.clear();
    }

    @Override
    public String getModuleName() {
        return "Network";
    }
}
