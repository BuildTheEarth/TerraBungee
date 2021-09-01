package net.buildtheearth.terrabungee.controller.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.noahhusby.lib.data.storage.Key;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Key("Id")
@AllArgsConstructor
public class StorableStaticInstance {
    @Expose
    @SerializedName("Id")
    public String id;
    @Expose
    @SerializedName("Address")
    public String address;
}
