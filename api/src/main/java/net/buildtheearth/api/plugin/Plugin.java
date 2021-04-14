package net.buildtheearth.api.plugin;

import lombok.Getter;

import java.util.logging.Logger;

/**
 * @author Noah Husby
 */
public abstract class Plugin {

    private PluginDescription description;
    @Getter
    private Logger logger;

    protected void init(PluginDescription description) {
        this.description = description;
        logger = new PluginLogger(description.getName());
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
