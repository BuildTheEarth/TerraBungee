package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.CommandSender;

public class AddInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getPurpose() {
        return "Add a static instance";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"<id>", "<address>"};
    }
}
