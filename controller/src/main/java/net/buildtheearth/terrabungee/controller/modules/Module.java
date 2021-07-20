package net.buildtheearth.terrabungee.controller.modules;

/**
 * @author Noah Husby
 */
public interface Module {
    /**
     * Called on module enable
     */
    void onEnable();

    /**
     * Called on module disable
     */
    void onDisable();

    /**
     * Gets name of module
     *
     * @return Name of module
     */
    String getModuleName();
}