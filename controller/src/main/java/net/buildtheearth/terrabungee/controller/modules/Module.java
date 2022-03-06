package net.buildtheearth.terrabungee.controller.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

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

    public abstract List<String> getRequiredModules();

    protected void fatal() {

    }

    protected void warning() {

    }
}