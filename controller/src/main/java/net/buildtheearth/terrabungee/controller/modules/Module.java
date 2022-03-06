package net.buildtheearth.terrabungee.controller.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public abstract class Module {

    @Getter
    private final String moduleName;

    /**
     * Called on module enable
     */
    public abstract void onEnable();

    /**
     * Called on module disable
     */
    public abstract void onDisable();



    protected void fatal() {

    }

    protected void warning() {

    }
}