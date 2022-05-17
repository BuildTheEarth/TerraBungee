package net.buildtheearth.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.NonNull;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.network.IC2SPacket;
import net.buildtheearth.api.network.IS2CPacket;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Noah Husby
 */
public final class PluginManager {

    private final TerraBungee controller;

    private final Yaml yaml;
    private final Map<String, Plugin> plugins = Maps.newLinkedHashMap();
    @Getter
    private final Map<String, Command> commandMap = Maps.newHashMap();
    private final Multimap<Plugin, Command> commandsByPlugin = ArrayListMultimap.create();
    @Getter
    private final Map<String, IS2CPacket> packetMap = Maps.newHashMap();
    private final Multimap<Plugin, IS2CPacket> packetsByPlugin = ArrayListMultimap.create();
    private Map<String, PluginDescription> toLoad = Maps.newHashMap();


    public PluginManager(TerraBungee controller) {
        this.controller = controller;
        Constructor yamlConstructor = new Constructor();
        PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        yamlConstructor.setPropertyUtils(propertyUtils);
        yaml = new Yaml(yamlConstructor);
    }

    public void registerCommand(Plugin plugin, Command command) {
        commandMap.put(command.getName().toLowerCase(Locale.ROOT), command);
        commandsByPlugin.put(plugin, command);
    }

    public void registerPacket(Plugin plugin, IS2CPacket packet) {
        packetMap.put(packet.getID(), packet);
        packetsByPlugin.put(plugin, packet);
    }

    public void unregisterCommand(Command command) {
        while (commandMap.values().remove(command)) {
        }
        commandsByPlugin.values().remove(command);
    }

    public void unregisterPacket(IC2SPacket packet) {
        while (packetMap.values().remove(packet)) {
        }
        packetsByPlugin.values().remove(packet);
    }

    public void unregisterCommands(Plugin plugin) {
        for (Iterator<Command> it = commandsByPlugin.get(plugin).iterator(); it.hasNext(); ) {
            Command command = it.next();
            while (commandMap.values().remove(command)) {
            }
            it.remove();
        }
    }

    public void unregisterPackets(Plugin plugin) {
        for (Iterator<IS2CPacket> it = packetsByPlugin.get(plugin).iterator(); it.hasNext(); ) {
            IS2CPacket packet = it.next();
            while (packetMap.values().remove(packet)) {
            }
            it.remove();
        }
    }

    private Command getCommandIfEnabled(String commandName) {
        String commandLower = commandName.toLowerCase(Locale.ROOT);
        return commandMap.get(commandLower);
    }

    public boolean dispatchCommand(String commandLine) {
        String[] split = commandLine.split(" ", -1);
        // Check for chat that only contains " "
        if (split.length == 0 || split[0].isEmpty()) {
            return false;
        }

        Command command = getCommandIfEnabled(split[0]);
        if (command == null) {
            return false;
        }

        String[] args = Arrays.copyOfRange(split, 1, split.length);
        try {
            command.execute(args);
        } catch (Exception e) {
            TerraBungee.getInstance().getLogger().warn("Error in dispatching command", e);
        }

        return true;
    }

    public void loadPlugins() {
        Map<PluginDescription, Boolean> pluginStatuses = Maps.newHashMap();
        for (Map.Entry<String, PluginDescription> entry : toLoad.entrySet()) {
            PluginDescription plugin = entry.getValue();
            if (!enablePlugin(pluginStatuses, plugin)) {
            }
        }
        toLoad.clear();
        toLoad = null;
    }

    public void enablePlugins() {
        for (Plugin plugin : plugins.values()) {
            try {
                plugin.onEnable();
            } catch (Throwable t) {
            }
        }
    }

    public boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses, PluginDescription plugin) {
        if (pluginStatuses.containsKey(plugin)) {
            return pluginStatuses.get(plugin);
        }

        try {
            URLClassLoader loader = new PluginClassloader(plugin, plugin.getFile(), null);
            Class<?> main = loader.loadClass(plugin.getMain());
            Plugin clazz = (Plugin) main.getDeclaredConstructor().newInstance();
            plugins.put(plugin.getName(), clazz);
            controller.getLogger().info(String.format("Loaded plugin %s version %s by %s", plugin.getName(), plugin.getVersion(), plugin.getAuthor()));
        } catch (Throwable ignored) {
        }

        pluginStatuses.put(plugin, true);
        return true;
    }

    public void detectPlugins(@NonNull File folder) {
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try (JarFile jar = new JarFile(file)) {
                    JarEntry pdf = jar.getJarEntry("terrabungee.yml");
                    Preconditions.checkNotNull(pdf, "terrabungee.yml");

                    try (InputStream in = jar.getInputStream(pdf)) {
                        PluginDescription desc = yaml.loadAs(in, PluginDescription.class);
                        Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", file);
                        Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", file);

                        desc.setFile(file);
                        toLoad.put(desc.getName(), desc);
                    }

                } catch (Exception e) {
                }
            }
        }
    }
}
