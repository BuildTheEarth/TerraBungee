package net.buildtheearth.terrabungee.controller.discord.commands.util;

import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.api.players.ControllerPlayer;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.buildtheearth.terrabungee.controller.players.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A discord command to list how many players are online on the network and where they are.
 *
 * @author MineFact
 */
public class ListDiscordCommand implements IDiscordCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "list how many players are on the network";
    }

    @Override
    public void execute(User user, UserPermission permission, OffsetDateTime executionTime, SlashCommandEvent event) {
        Map<UUID, ControllerPlayer> players = PlayerManager.getInstance().getOnlinePlayerRegistry();
        HashMap<String, List<ControllerPlayer>> list = new HashMap<>();

        //Loop through all online players
        for (UUID uuid : players.keySet()) {
            ControllerPlayer player = players.get(uuid);

            if (player.getServer() == null) {
                continue;
            }

            //Map them per Server
            List<ControllerPlayer> serverList = list.get(player.getServer());

            if (serverList == null) {
                serverList = new ArrayList<>();
            }

            serverList.add(player);

            list.remove(player.getServer());
            list.put(player.getServer(), serverList);
        }


        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("SERVER PLAYER LIST:");
        e.setTimestamp(new Date().toInstant());
        e.setFooter("IP: BuildTheEarth.net");

        for (String server : list.keySet()) {
            List<ControllerPlayer> serverList = list.get(server);

            if (serverList == null) {
                continue;
            }

            StringBuilder playerString = new StringBuilder();
            for (ControllerPlayer player : serverList) {
                playerString.append("- ").append(player.getName());

                if (player.getDiscordId() != null) {
                    playerString.append(" (<@").append(player.getDiscordId()).append(">)");
                }

                playerString.append("\n");
            }

            e.addField(new MessageEmbed.Field(server, playerString.toString(), false));

        }

        e.addField(new MessageEmbed.Field("", "`Total: " + PlayerManager.getInstance().getTotalOnlinePlayers() + " Players`", false));

        event.replyEmbeds(e.build()).submit();

    }

    @Override
    public void configureData(CommandData data) {

    }
}
