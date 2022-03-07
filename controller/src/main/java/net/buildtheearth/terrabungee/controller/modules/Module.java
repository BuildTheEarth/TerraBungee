package net.buildtheearth.terrabungee.controller.modules;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Noah Husby
 */
public abstract class Module {

    @Getter
    private final String moduleName;

    @Getter
    private final Logger logger;

    public Module(String moduleName) {
        this.moduleName = moduleName;
        this.logger = LoggerFactory.getLogger(moduleName);
    }

    /**
     * Called on module enable
     */
    public abstract void onEnable();

    /**
     * Called on module disable
     */
    public abstract void onDisable();

    public abstract List<String> getRequiredModules();

    protected void fatal() {

    }

    protected void warning() {

    }
}