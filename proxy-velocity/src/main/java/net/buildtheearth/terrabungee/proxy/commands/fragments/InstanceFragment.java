/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - InstanceFragment.java
 */

package net.buildtheearth.terrabungee.proxy.commands.fragments;

import com.velocitypowered.api.command.SimpleCommand;
import net.buildtheearth.terrabungee.proxy.commands.FragmentManager;
import net.buildtheearth.terrabungee.proxy.commands.ICommandFragment;
import net.buildtheearth.terrabungee.proxy.commands.fragments.instance.AddInstanceFragment;
import net.buildtheearth.terrabungee.proxy.commands.fragments.instance.InstanceInfoFragment;
import net.buildtheearth.terrabungee.proxy.commands.fragments.instance.ListInstanceFragment;
import net.buildtheearth.terrabungee.proxy.commands.fragments.instance.RemoveInstanceFragment;
import com.velocitypowered.api.command.CommandSource;


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
    public void execute(SimpleCommand.Invocation invocation) {
        executeFragment(invocation);
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
