package com.noahhusby.terrabungee;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.noahhusby.terrabungee.api.Plugin;
import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.plugins.PluginClassloader;
import com.noahhusby.terrabungee.controller.plugins.PluginDescription;
import lombok.NonNull;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * @author Noah Husby
 */
public final class PluginManager {

    private final TerraBungeeController controller;

    private final Yaml yaml;
    private final Map<String, Plugin> plugins = Maps.newLinkedHashMap();
    private Map<String, PluginDescription> toLoad = Maps.newHashMap();

    public PluginManager(TerraBungeeController controller) {
        this.controller = controller;
        Constructor yamlConstructor = new Constructor();
        PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        yamlConstructor.setPropertyUtils(propertyUtils);
        yaml = new Yaml(yamlConstructor);

    }

    public void loadPlugins() {
        Map<PluginDescription, Boolean> pluginStatuses = Maps.newHashMap();
        for ( Map.Entry<String, PluginDescription> entry : toLoad.entrySet() )
        {
            PluginDescription plugin = entry.getValue();
            if ( !enablePlugin( pluginStatuses, plugin ) )
            {
            }
        }
        toLoad.clear();
        toLoad = null;
    }

    public void enablePlugins() {
        for ( Plugin plugin : plugins.values() )
        {
            try
            {
                plugin.onEnable();
            } catch ( Throwable t )
            {
            }
        }
    }

    public boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses, PluginDescription plugin) {
        if(pluginStatuses.containsKey(plugin)) {
            return pluginStatuses.get(plugin);
        }

        try {
            URLClassLoader loader = new PluginClassloader(plugin, plugin.getFile(), null);
            Class<?> main = loader.loadClass( plugin.getMain() );
            Plugin clazz = (Plugin) main.getDeclaredConstructor().newInstance();
            plugins.put( plugin.getName(), clazz );
            TerraBungeeController.logger.info(String.format("Loaded plugin %s version %s by %s", plugin.getName(), plugin.getVersion(), plugin.getAuthor()));
        } catch (Throwable ignored) {
        }

        pluginStatuses.put( plugin, true );
        return true;
    }

    public void detectPlugins(@NonNull File folder) {
        for(File file : folder.listFiles()) {
            if(file.isFile() && file.getName().endsWith(".jar")) {
                try (JarFile jar = new JarFile(file)) {
                    JarEntry pdf = jar.getJarEntry( "terrabungee.yml" );
                    Preconditions.checkNotNull( pdf, "terrabungee.yml" );

                    try ( InputStream in = jar.getInputStream( pdf ) )
                    {
                        PluginDescription desc = yaml.loadAs( in, PluginDescription.class );
                        Preconditions.checkNotNull( desc.getName(), "Plugin from %s has no name", file );
                        Preconditions.checkNotNull( desc.getMain(), "Plugin from %s has no main", file );

                        desc.setFile( file );
                        toLoad.put( desc.getName(), desc );
                    }

                } catch (Exception e) {

                }
            }
        }
    }
}
