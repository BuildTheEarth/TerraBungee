/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeAPI - APIManager.java
 */

package net.buildtheearth.terrabungee.client.util;

import net.buildtheearth.terrabungee.client.TerraBungeeClient;

/**
 * @author Noah Husby
 */
public abstract class Manager {
    protected final TerraBungeeClient tb;

    public Manager(TerraBungeeClient tb) {
        this.tb = tb;
    }
}
