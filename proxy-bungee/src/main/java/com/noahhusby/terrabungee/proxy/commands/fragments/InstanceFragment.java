/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - InstanceFragment.java
 */

package com.noahhusby.terrabungee.proxy.commands.fragments;

import com.noahhusby.terrabungee.proxy.commands.FragmentManager;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import com.noahhusby.terrabungee.proxy.commands.fragments.instance.AddInstanceFragment;
import com.noahhusby.terrabungee.proxy.commands.fragments.instance.InstanceInfoFragment;
import com.noahhusby.terrabungee.proxy.commands.fragments.instance.ListInstanceFragment;
import com.noahhusby.terrabungee.proxy.commands.fragments.instance.RemoveInstanceFragment;
import net.md_5.bungee.api.CommandSender;

public class InstanceFragment extends FragmentManager implements ICommandFragment {

    public InstanceFragment() {
        setCommandBase("tba instance");
        setTitle("TerraBungee Instance Commands");
        registerCommandFragment(new InstanceInfoFragment());
        registerCommandFragment(new ListInstanceFragment());
        registerCommandFragment(new AddInstanceFragment());
        registerCommandFragment(new RemoveInstanceFragment());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        executeFragment(sender, args);
    }

    @Override
    public String getName() {
        return "instance";
    }

    @Override
    public String getPurpose() {
        return "Configure instances";
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
