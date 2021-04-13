package com.noahhusby.someplugin;

import com.noahhusby.terrabungee.api.Plugin;

/**
 * @author Noah Husby
 */
public class MyPlugin extends Plugin {
    @Override
    public void onEnable() {
        System.out.println("My plugin is working!!");
    }

    @Override
    public void onDisable() {

    }
}
