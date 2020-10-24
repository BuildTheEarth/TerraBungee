package com.noahhusby.terrabungee.api.network;

public class Response {

    public final PacketResponse response;
    public final String salt;
    public final long time;
    public final int timeout;

    public Response(PacketResponse response, String salt, long time, int timeout) {
        this.response = response;
        this.salt = salt;
        this.time = time;
        this.timeout = timeout;
    }

    public enum ResponseCode {
        TIMED_OUT, ERROR, SUCCESS
    }
}
