package com.noahhusby.terrabungee.api.network.C2P;

import com.noahhusby.terrabungee.api.Constants;
import com.noahhusby.terrabungee.api.TerraBungee;
import com.noahhusby.terrabungee.api.network.IC2SPacket;
import com.noahhusby.terrabungee.api.services.Instance;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class C2SInstanceUpdatePacket implements IC2SPacket {
    @Override
    public String getType() {
        return Constants.instanceUpdateID;
    }

    @Override
    public void onMessage(TerraBungee tb, JSONObject data) {
        JSONArray instanceArray = (JSONArray) data.get("instances");
        List<Instance> instances = new ArrayList<>();
        for(Object o : instanceArray) {
            JSONObject instance = (JSONObject) o;
            instances.add(Instance.fromJSON(instance));
        }

        tb.getInstanceManager().setInstances(instances);
    }
}
