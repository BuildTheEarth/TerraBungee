package net.buildtheearth.api.plugin;

import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Noah Husby
 */
public abstract class Plugin {

    private PluginDescription description;
    @Getter
    private Logger logger;
    @Getter
    private File pluginFolder;

    public Plugin() {
        ClassLoader classLoader = getClass().getClassLoader();
        ((PluginClassloader) classLoader).init(this);
    }

    protected void init(PluginDescription description) {
        this.description = description;
        logger = LoggerFactory.getLogger(description.getName());
        pluginFolder = new File(TerraBungee.getInstance().getPluginFolder(), description.getName());
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
