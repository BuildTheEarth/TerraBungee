package com.noahhusby.terrabungee.controller.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StorableStaticInstance {
    @Expose
    @SerializedName("Id")
    public String id;
    @Expose
    @SerializedName("Address")
    public String address;

    public StorableStaticInstance(String id, String address) {
        this.id = id;
        this.address = address;
    }

}
