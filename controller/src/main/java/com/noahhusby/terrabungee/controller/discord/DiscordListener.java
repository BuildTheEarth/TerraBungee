package com.noahhusby.terrabungee.controller.discord;

import com.noahhusby.terrabungee.controller.Constants;
import com.noahhusby.terrabungee.controller.discord.commands.DiscordCommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getAuthor().isBot()) return;
        if(e.getMessage().getContentRaw().startsWith(Constants.discordPrefix)) {
            String[] message = e.getMessage().getContentRaw().replace(Constants.discordPrefix, "").split(" ");
            if(message.length == 0) return;
            String[] args = message.length == 1 ? new String[]{} : selectArray(message, 1);
            DiscordCommandManager.getInstance().execute(message[0], e.getAuthor(), e.getMessage().getTextChannel(), e.getMessage().getTimeCreated(), args);
        }
    }

    /**
     * Gets all objects in a string array above a given index
     * @param args Initial array
     * @param index Starting index
     * @return Selected array
     */
    private String[] selectArray(String[] args, int index) {
        List<String> array = new ArrayList<>();
        for(int i = index; i < args.length; i++)
            array.add(args[i]);

        return array.toArray(array.toArray(new String[array.size()]));
    }
}
