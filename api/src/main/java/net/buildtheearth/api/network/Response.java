package net.buildtheearth.api.network;

import com.google.gson.JsonObject;

public class Response {
    private final ServicePacket servicePacket;
    private final String salt;
    private net.buildtheearth.terrabungee.common.network.Response.ResponseCode responseCode = net.buildtheearth.terrabungee.common.network.Response.ResponseCode.SUCCESS;
    private JsonObject responseData = new JsonObject();

    public Response(ServicePacket sp, String salt) {
        this.servicePacket = sp;
        this.salt = salt;
    }

    public ServicePacket getServicePacket() {
        return servicePacket;
    }

    public String getSalt() {
        return salt;
    }

    public net.buildtheearth.terrabungee.common.network.Response.ResponseCode getCode() {
        return responseCode;
    }

    public void setCode(net.buildtheearth.terrabungee.common.network.Response.ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public JsonObject getData() {
        return responseData;
    }

    public void setData(JsonObject responseData) {
        this.responseData = responseData;
    }
}
