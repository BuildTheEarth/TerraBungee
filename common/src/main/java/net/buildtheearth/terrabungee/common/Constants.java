package net.buildtheearth.terrabungee.common;

import net.buildtheearth.terrabungee.common.exceptions.VersionParseException;

/**
 * @author Noah Husby
 */
public class Constants {
    public static final TerraBungeeVersion VERSION;

    static {
        TerraBungeeVersion ver;
        try {
            ver = new TerraBungeeVersion(Constants.class.getPackage().getImplementationVersion());
        } catch (VersionParseException exception) {
            ver = new TerraBungeeVersion(0, 0, 0, true);
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

    /*
     * Punishment Packets
     */
    public static final String retrieveActiveBanID = "punishment_is_banned";
    public static final String proxyBanDisconnectID = "proxy_ban_disconnect";
    public static final String proxyKickDisconnectID = "proxy_kick_disconnect";
    public static final String banPlayerID = "punishment_ban_player";
    public static final String kickPlayerID = "punishment_kick_player";
    public static final String mutePlayerID = "punishment_mute_player";
    public static final String retrievePunishmentsID = "punishments_retrieve";
    public static final String retrievePunishmentID = "punishment_retrieve";
    public static final String editPunishmentID = "edit_punishment";

    public static final String serviceMessageID = "service_message";
}
