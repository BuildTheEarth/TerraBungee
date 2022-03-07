package net.buildtheearth.api;

import lombok.Getter;
import net.buildtheearth.api.network.INetworkManager;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.api.plugin.PluginManager;
import org.slf4j.Logger;

import java.io.File;
import java.util.UUID;

/**
 * @author Noah Husby
 */
public abstract class TerraBungee {
    @Getter
    private static TerraBungee instance;

    public static void setInstance(TerraBungee instance) {
        TerraBungee.instance = instance;
    }

    protected abstract void start();

    /**
     * Shuts down the current controller
     */
    public abstract void end();

    /**
     * Gets the version of the current controller.
     *
     * @return the version of this controller
     */
    public abstract String getVersion();

    /**
     * Gets the logger for the controller
     *
     * @return the {@link Logger} instance
     */
    public abstract Logger getLogger();

    /**
     * Gets the plugin manager for the controller
     *
     * @return the {@link PluginManager instance}
     */
    public abstract PluginManager getPluginManager();

    /**
     * Gets the network manager for the controller
     *
     * @return the {@link INetworkManager} instance
     */
    public abstract INetworkManager getNetworkManager();

    /**
     * Gets {@link ControllerPlayer} from the controller
     *
     * @param uuid UUID of player
     * @return {@link ControllerPlayer}
     */
    public abstract ControllerPlayer getPlayer(UUID uuid);

    /**
     * Gets {@link ControllerPlayer} from the controller
     *
     * @param username Username of player
     * @return {@link ControllerPlayer}
     */
    public abstract ControllerPlayer getPlayer(String username);

    /**
     * Gets root folder of controller
     *
     * @return root {@link File} of controller
     */
    public abstract File getFolder();

    /**
     * Gets plugin folder of controller
     *
     * @return plugin {@link File} of controller
     */
    public abstract File getPluginFolder();
}
