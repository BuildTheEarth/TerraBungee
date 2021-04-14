package net.buildtheearth.terrabungee.controller.discord;

import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.controller.ControllerConstants;
import net.buildtheearth.terrabungee.controller.discord.commands.DiscordCommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        if (e.getMessage().getContentRaw().startsWith(ControllerConstants.discordPrefix)) {
            String[] message = e.getMessage().getContentRaw().replace(ControllerConstants.discordPrefix, "").split(" ");
            if (message.length == 0) {
                return;
            }
            String[] args = message.length == 1 ? new String[]{} : selectArray(message, 1);

            UserPermission permission = UserPermission.NONE;
            DiscordConfig config = DiscordManager.getInstance().getConfigByGuild(e.getMessage().getGuild());
            Member m = e.getMessage().getMember();

            if (m != null) {
                for (Role r : m.getRoles()) {
                    if (r.getName().equalsIgnoreCase("TBAdmin")) {
                        permission = UserPermission.ADMIN;
                        break;
                    }
                }

                if (config != null && permission != UserPermission.ADMIN) {
                    for (Role r : m.getRoles()) {
                        if (config.getAdminRoles().contains(r.getIdLong())) {
                            comparePermission(permission, UserPermission.ADMIN);
                        }

                        if (config.getModeratorRoles().contains(r.getIdLong())) {
                            comparePermission(permission, UserPermission.MODERATOR);
                        }

                        if (config.getStandardRoles().contains(r.getIdLong())) {
                            comparePermission(permission, UserPermission.STANDARD);
                        }
                    }
                }
            }


            DiscordCommandManager.getInstance().execute(message[0], permission, e.getAuthor(), e.getMessage().getTextChannel(), e.getMessage().getTimeCreated(), args);
        }
    }

    /**
     * Gets all objects in a string array above a given index
     *
     * @param args  Initial array
     * @param index Starting index
     * @return Selected array
     */
    private String[] selectArray(String[] args, int index) {
        List<String> array = new ArrayList<>();
        for (int i = index; i < args.length; i++) {
            array.add(args[i]);
        }

        return array.toArray(array.toArray(new String[array.size()]));
    }

    private void comparePermission(UserPermission current, UserPermission updated) {
        switch (updated) {
            case ADMIN:
                current = UserPermission.ADMIN;
                break;
            case MODERATOR:
                if (current != UserPermission.ADMIN) {
                    current = UserPermission.MODERATOR;
                }
                break;
            case STANDARD:
                if (current != UserPermission.ADMIN && current != UserPermission.MODERATOR) {
                    current = UserPermission.STANDARD;
                }
        }
    }
}
