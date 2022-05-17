package net.buildtheearth.terrabungee.controller.modules;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;

import java.util.Map;

/**
 * @author Noah Husby
 */
public class ModuleHandler {
    @Getter
    private static final ModuleHandler instance = new ModuleHandler();
    @Getter
    private final Map<Module, Boolean> modules = Maps.newLinkedHashMap();

    private ModuleHandler() {
    }

    /**
     * Register a new module
     *
     * @param module {@link Module}
     */
    public void registerModule(Module module) {
        modules.put(module, false);
    }

    /**
     * Registers an array of modules
     *
     * @param modules {@link Module}
     */
    public void registerModules(Module... modules) {
        for (Module m : modules) {
            registerModule(m);
        }
    }

    /**
     * Unregisters a specific module
     *
     * @param module
     */
    public void unregisterModule(Module module) {
        modules.remove(module);
    }

    /**
     * Unregisters all modules
     */
    public void unregisterModules() {
        modules.clear();
    }

    /**
     * Enables a specific module
     *
     * @param module {@link Module}
     * @return True if successfully enabled, false if not
     */
    public boolean enable(Module module) {
        if (modules.get(module)) {
            return false;
        }
        if (!module.getRequiredModules().isEmpty()) {
            for (String required : module.getRequiredModules()) {
                for (Module requiredMod : modules.keySet()) {
                    if (requiredMod.getModuleName().equalsIgnoreCase(required)) {
                        // TODO: Clean up and throw exception if loop
                        enable(requiredMod);
                    }
                }
            }
        }
        long start = System.currentTimeMillis();
        module.onEnable();
        modules.put(module, true);
        TerraBungeeController.logger.info(String.format("Enabled Module: %s (%d)", module.getModuleName(), System.currentTimeMillis() - start));
        return true;
    }

    /**
     * Disables a specific module
     *
     * @param module {@link Module}
     * @return True if successfully disabled, false if not
     */
    public boolean disable(Module module) {
        if (!modules.get(module)) {
            return false;
        }
        long start = System.currentTimeMillis();
        module.onDisable();
        modules.put(module, false);
        TerraBungeeController.logger.info(String.format("Disabled Module: %s (%d)", module.getModuleName(), System.currentTimeMillis() - start));
        return true;
    }

    /**
     * Enables all modules
     */
    public void enableAll() {
        for (Map.Entry<Module, Boolean> e : ImmutableMap.copyOf(modules).entrySet()) {
            if (!e.getValue()) {
                enable(e.getKey());
            }
        }
    }

    /**
     * Disables all modules
     */
    public void disableAll() {
        for (Map.Entry<Module, Boolean> e : ImmutableMap.copyOf(modules).entrySet()) {
            if (e.getValue()) {
                disable(e.getKey());
            }
        }
    }
}
