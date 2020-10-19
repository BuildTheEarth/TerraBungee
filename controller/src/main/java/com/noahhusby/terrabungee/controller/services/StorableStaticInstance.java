package com.noahhusby.terrabungee.controller.services;

import com.google.gson.annotations.Expose;

public class StorableStaticInstance {
    @Expose
    public String id;
    @Expose
    public String address;

    public StorableStaticInstance(String id, String address) {
        this.id = id;
        this.address = address;
    }

}
