package net.buildtheearth.api.players;

import com.noahhusby.lib.data.storage.Key;
import net.buildtheearth.terrabungee.common.players.TBPlayer;

import java.util.Map;
import java.util.UUID;

@Key("UUID")
public class ControllerPlayer extends TBPlayer {
    public ControllerPlayer(UUID uuid) {
        super(uuid);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setDiscordId(String id) {
        this.discordId = id;
    }

    public void setProxy(String id) {
        this.proxy = id;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setOnline(boolean online) {
        this.online = online;
        if (!online) {
            setServer(null);
            setProxy(null);
        } else {
            setLastSeen(System.currentTimeMillis());
        }
    }

    public ControllerPlayer deepCopy() {
        ControllerPlayer copy = new ControllerPlayer(getUniqueID());
        copy.setOnline(online);
        copy.setDiscordId(discordId);
        copy.setName(name);
        copy.setAttributes(attributes);
        copy.setProxy(proxy);
        copy.setServer(server);
        copy.setLastSeen(lastSeen);


        return copy;
    }
}
