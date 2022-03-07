package net.buildtheearth.terrabungee.controller.services;

import com.google.gson.annotations.Expose;
import com.noahhusby.lib.data.storage.Key;
import lombok.AllArgsConstructor;

@Key("id")
@AllArgsConstructor
public class StorableStaticInstance {
    @Expose
    public String id;
    @Expose
    public String address;
}
