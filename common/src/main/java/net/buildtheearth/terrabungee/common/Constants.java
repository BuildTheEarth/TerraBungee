package net.buildtheearth.terrabungee.common;

import java.util.Properties;

/**
 * @author Noah Husby
 */
public class Constants {
    public static final String VERSION;

    static {
        Properties versionProperties = new Properties();
        String ver = "[Development Build]";
        try {
            versionProperties.load(Constants.class.getResourceAsStream("/version.properties"));
            ver = versionProperties.getProperty("version");
        } catch (Exception ignored) {
        }
        VERSION = ver;
    }

    public static final int serviceTimeout = 5000;
    public static final int responseTimeout = 1000;

    public static final String serviceInitID = "service_init";
    public static final String setServiceStatusID = "set_service_status";
    public static final String keepAliveID = "keep_alive";

    /*
     * Instance Packets
     */
    public static final String instanceUpdateID = "instance_update";
    public static final String responseID = "packet_response";
    public static final String addStaticInstanceID = "add_static_instance";
    public static final String removeStaticInstanceID = "remove_static_instance";

    /*
     * Player Packets
     */
    public static final String updateAttributeID = "update_attribute";
    public static final String retrieveUncachedPlayerID = "retrieve_uncached_player";
    public static final String retrieveUncachedPlayersID = "retrieve_uncached_players";
    public static final String onlinePlayerCacheHit = "online_player_cache";
    public static final String playerJoinEventID = "player_join_event";
    public static final String playerQuitEventID = "player_quit_event";

    public static final String serviceMessageID = "service_message";
}
