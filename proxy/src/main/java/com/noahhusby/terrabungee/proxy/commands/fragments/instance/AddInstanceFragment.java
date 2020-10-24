package com.noahhusby.terrabungee.proxy.commands.fragments.instance;

import com.noahhusby.terrabungee.api.network.Response;
import com.noahhusby.terrabungee.api.network.S2C.S2CKeepAlivePacket;
import com.noahhusby.terrabungee.api.network.S2C.S2CResponsePacket;
import com.noahhusby.terrabungee.proxy.TerraBungeeProxyMain;
import com.noahhusby.terrabungee.proxy.chat.ChatHelper;
import com.noahhusby.terrabungee.proxy.chat.TextElement;
import com.noahhusby.terrabungee.proxy.commands.ICommandFragment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class AddInstanceFragment implements ICommandFragment {
    @Override
    public void execute(CommandSender sender, String[] args) {
        TerraBungeeProxyMain.tb.getNetworkManager().send(new S2CResponsePacket(new S2CKeepAlivePacket(),
                (responseCode, jsonObject) -> {
                    if(responseCode == Response.ResponseCode.TIMED_OUT) {
                        sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement("You got fucked", ChatColor.RED)));
                        return;
                    }

                    sender.sendMessage(ChatHelper.makeTitleTextComponent(new TextElement(responseCode.name(), ChatColor.RED)));
                }));
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
