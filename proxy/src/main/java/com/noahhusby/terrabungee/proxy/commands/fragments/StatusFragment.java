package com.noahhusby.terrabungee.proxy.commands.fragments;

import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.CommandSender;

public class StatusFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getPurpose() {
        return "Shows status of the TB System";
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
