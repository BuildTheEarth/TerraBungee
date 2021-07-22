package net.buildtheearth.terrabungee.controller.discord;

import net.buildtheearth.api.discord.UserPermission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent e) {
        if (e.getGuild() == null) {
            return;
        }
        UserPermission permission = UserPermission.NONE;
        GuildConfig config = DiscordManager.getInstance().getConfigByGuild(e.getGuild());
        Member m = e.getMember();

        if (m != null) {
            for (Role r : m.getRoles()) {
                if (r.getName().equalsIgnoreCase("TBAdmin")) {
                    permission = UserPermission.ADMIN;
                    break;
                }
            }

            if (config != null && permission != UserPermission.ADMIN) {
                for (Role r : m.getRoles()) {
                    if (config.getStaffRoles().contains(r.getIdLong())) {
                        comparePermission(permission, UserPermission.ADMIN);
                    }
                }
            }
        }
        DiscordManager.getInstance().executeSlashCommand(e.getName(), permission, e.getUser(), e.getTimeCreated(), e);
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        DiscordManager.getInstance().executeButtonCommand(event);
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
