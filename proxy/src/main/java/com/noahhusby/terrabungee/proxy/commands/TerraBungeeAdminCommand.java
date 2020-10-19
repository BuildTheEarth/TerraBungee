package com.noahhusby.terrabungee.proxy.commands;

import com.noahhusby.terrabungee.proxy.chat.ChatHelper;
import com.noahhusby.terrabungee.proxy.chat.TextElement;
import com.noahhusby.terrabungee.proxy.commands.fragments.InstanceFragment;
import com.noahhusby.terrabungee.proxy.commands.fragments.StatusFragment;
import com.noahhusby.terrabungee.proxy.commands.fragments.instance.ListInstanceFragment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class TerraBungeeAdminCommand extends CommandFragmentManager {
    public TerraBungeeAdminCommand() {
        super("terrabungeeadmin", "", new String[]{"tba"});
        setCommandBase("tba");
        setTitle("TerraBungee Admin Commands");

        registerCommandFragment(new StatusFragment());
        registerCommandFragment(new InstanceFragment());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!hasAdmin(sender)) {
            sender.sendMessage(ChatHelper.makeTextComponent(new TextElement("You don't have permission to run this command!", ChatColor.DARK_RED)));
            return;
        }

        executeFragment(sender, args);
    }
}
