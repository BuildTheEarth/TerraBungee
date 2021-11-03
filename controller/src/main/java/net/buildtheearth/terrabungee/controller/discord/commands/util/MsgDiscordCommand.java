package net.buildtheearth.terrabungee.controller.discord.commands.util;

import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * A discord command to message an ingame player.
 *
 * @author MineFact
 */
public class MsgDiscordCommand implements IDiscordCommand {
    @Override
    public String getName() {
        return "msg";
    }

    @Override
    public String getDescription() {
        return "message a player on the network";
    }

    @Override
    public void execute(User user, UserPermission permission, OffsetDateTime executionTime, SlashCommandEvent event) {
        Map<UUID, ControllerPlayer> players = PlayerManager.getInstance().getOnlinePlayerRegistry();

        event.reply("Done.").submit();
    }

    @Override
    public void configureData(CommandData data) {
        Map<UUID, ControllerPlayer> players = PlayerManager.getInstance().getOnlinePlayerRegistry();

        for(UUID uuid : players.keySet()){
            ControllerPlayer player = players.get(uuid);

            data.addOption(OptionType.STRING, player.getName(), "Test");
        }

    }
}
