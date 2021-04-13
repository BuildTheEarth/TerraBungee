package com.noahhusby.terrabungee.controller.plugins;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @author Noah Husby
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginDescription {
    /**
     * Friendly name of the plugin.
     */
    private String name;
    /**
     * Plugin main class. Needs to extend {@link Plugin}.
     */
    private String main;
    /**
     * Plugin version.
     */
    private String version;
    /**
     * Plugin author.
     */
    private String author;
    /**
     * File we were loaded from.
     */
    private File file = null;
}
