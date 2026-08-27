package net.buildtheearth.terrabungee.controller.network.S2C;

import com.google.gson.JsonObject;
import net.buildtheearth.api.network.IS2CPacket;
import net.buildtheearth.api.network.Response;
import net.buildtheearth.api.network.ServicePacket;
import net.buildtheearth.terrabungee.common.Constants;
import net.buildtheearth.terrabungee.common.network.Response.ResponseCode;
import net.buildtheearth.terrabungee.common.services.ServiceStatus;
import net.buildtheearth.terrabungee.common.services.TerraBungeeService;
import net.buildtheearth.terrabungee.controller.network.C2S.C2SServiceMessagePacket;
import net.buildtheearth.terrabungee.controller.network.NetworkManager;
import net.buildtheearth.terrabungee.controller.services.ServiceManager;

/** This is a service message packet that is sent from the proxy server to the controller.
 * The controller will forward the message to the target service.
 * <p>
 * Service Message Types:<br>
 * - DIRECT_MESSAGE<br>
 * - DIRECT_MESSAGE_RECEIVED<br>
 * - DIRECT_MESSAGE_FAILED<br>
 * - BROADCAST<br>
 * - GLOBAL_MESSAGE<br>
 * - BAN<br>
 * - WARNING<br>
 * - STAFF<br>
 * - PREMIUM<br>
 * **/
public class S2CServiceMessagePacket implements IS2CPacket {
    @Override
    public String getID() {
        return Constants.serviceMessageID;
    }

    @Override
    public void onMessage(ServicePacket servicePacket, JsonObject data, Response response) {
        String to = data.get("to").getAsString();
        TerraBungeeService target = ServiceManager.getInstance().getService(to);
        if (target == null || target.getStatus() != ServiceStatus.ONLINE) {
            response.setCode(ResponseCode.ERROR);
            return;
        }

        boolean sent = NetworkManager.getInstance().trySend(
                new C2SServiceMessagePacket(servicePacket.getId(), to, data.get("message").getAsString()));
        response.setCode(sent ? ResponseCode.SUCCESS : ResponseCode.ERROR);
    }
}
