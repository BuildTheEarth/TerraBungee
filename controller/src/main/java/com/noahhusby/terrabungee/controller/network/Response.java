package com.noahhusby.terrabungee.controller.network;

import com.noahhusby.terrabungee.api.services.ITerraBungeeService;
import com.noahhusby.terrabungee.controller.services.ServiceManager;
import org.json.simple.JSONObject;

public class Response {
    public final JSONObject responseData = new JSONObject();
    public final ITerraBungeeService service;
    public final String salt;

    public com.noahhusby.terrabungee.api.network.Response.ResponseCode responseCode = com.noahhusby.terrabungee.api.network.Response.ResponseCode.SUCCESS;

    public Response(ServicePacket sp, String salt) {
        this.service = ServiceManager.getInstance().getService(sp.getID());
        this.salt = salt;
    }
}
