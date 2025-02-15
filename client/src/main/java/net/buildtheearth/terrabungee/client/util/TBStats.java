/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - TBStats.java
 */

package net.buildtheearth.terrabungee.client.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.Constants;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class TBStats {
    private final String controllerVersion;
    @Getter
    private final int totalServices;
    @Getter
    private final int totalDisconnectedServices;
    @Getter
    private final int totalPlayers;
    @Getter
    private final int totalOnlinePlayers;
    private ControllerStats controllerStats;

    public ControllerStats getControllerStats() {
        if (controllerStats == null) {
            controllerStats = new ControllerStats(controllerVersion);
        }
        return controllerStats;
    }

    public static class ControllerStats {
        @Getter
        private final long lastSeen = System.currentTimeMillis();
        @Getter
        private final String version;

        private ControllerStats(String version) {
            this.version = version;
        }

        public boolean isOnline() {
            return lastSeen + Constants.serviceTimeout > System.currentTimeMillis();
        }
    }
}
