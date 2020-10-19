package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.CommandSender;

public class RemoveInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getPurpose() {
        return "Remove a static instance";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"<id>"};
    }
}
