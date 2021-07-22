package net.buildtheearth.terrabungee.controller.discord;

import net.buildtheearth.api.discord.UserPermission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent e) {
        if (e.getGuild() == null) {
            return;
        }
        if(!DiscordManager.getInstance().getGuildConfigs().containsKey(e.getGuild().getIdLong())) {
            return;
        }

        UserPermission permission = UserPermission.NONE;
        GuildConfig config = DiscordManager.getInstance().getConfigByGuild(e.getGuild());
        Member m = e.getMember();

        boolean hasPerms = false;
        if (m == null) {
            return;
        }
        for(Role r : m.getRoles()) {
            if(config.getStaffRoles().contains(r.getIdLong())) {
                hasPerms = true;
                break;
            }
        }
        if(!hasPerms) {
            e.reply("You don't have permission to run this command!").setEphemeral(true).submit();
            return;
        }
        DiscordManager.getInstance().executeSlashCommand(e.getName(), permission, e.getUser(), e.getTimeCreated(), e);
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getGuild() == null) {
            return;
        }
        if(!DiscordManager.getInstance().getGuildConfigs().containsKey(event.getGuild().getIdLong())) {
            return;
        }

        GuildConfig config = DiscordManager.getInstance().getConfigByGuild(event.getGuild());
        Member m = event.getMember();
        boolean hasPerms = false;
        if (m == null) {
            return;
        }
        for(Role r : m.getRoles()) {
            if(config.getStaffRoles().contains(r.getIdLong())) {
                hasPerms = true;
                break;
            }
        }
        if(!hasPerms) {
            event.reply("You don't have permission to run this command!").setEphemeral(true).submit();
            return;
        }
        DiscordManager.getInstance().executeButtonCommand(event);
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
