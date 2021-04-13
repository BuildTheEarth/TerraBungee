package net.buildtheearth.terrabungee.controller;

import net.buildtheearth.api.TerraBungee;

public class TerraBungeeLauncher {
    public static void main(String[] args) {
        TerraBungeeController controller = new TerraBungeeController();
        TerraBungee.setInstance(controller);
        controller.start();
    }
}
