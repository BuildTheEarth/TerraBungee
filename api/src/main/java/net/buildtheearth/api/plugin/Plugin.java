package net.buildtheearth.api.plugin;

import lombok.Getter;
import net.buildtheearth.api.TerraBungee;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Noah Husby
 */
public abstract class Plugin {

    private PluginDescription description;
    @Getter
    private Logger logger;
    @Getter
    private File pluginFolder;

    protected void init(PluginDescription description) {
        this.description = description;
        logger = new PluginLogger(description.getName());
        pluginFolder = new File(TerraBungee.getInstance().getFolder(), description.getName());
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
