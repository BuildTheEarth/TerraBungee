package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.CommandSender;

public class InstanceInfoFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getPurpose() {
        return "Display info about an instance";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"<id>"};
    }
}
