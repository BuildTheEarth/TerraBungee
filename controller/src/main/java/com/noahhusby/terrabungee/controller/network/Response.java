package com.noahhusby.terrabungee.controller.network;

import com.google.gson.JsonObject;
import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import com.noahhusby.terrabungee.api.network.Response.ResponseCode;

public class Response {
    private final ServicePacket servicePacket;
    private final String salt;
    private ResponseCode responseCode = ResponseCode.SUCCESS;
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

    public ResponseCode getCode() {
        return responseCode;
    }

    public void setCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public JsonObject getData() {
        return responseData;
    }

    public void setData(JsonObject responseData) {
        this.responseData = responseData;
    }
}
