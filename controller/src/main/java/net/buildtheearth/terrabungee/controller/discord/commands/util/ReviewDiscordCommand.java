package net.buildtheearth.terrabungee.controller.discord.commands.util;

import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.buildtheearth.terrabungee.controller.util.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;

/**
 * A discord command to list how many projects are unreviewed on each building server.
 *
 * @author MineFact
 */
public class ReviewDiscordCommand implements IDiscordCommand {
    @Override
    public String getName() {
        return "review";
    }

    @Override
    public String getDescription() {
        return "list how many projects are there to review";
    }

    @Override
    public void execute(User user, UserPermission permission, OffsetDateTime executionTime, SlashCommandInteractionEvent event) {
        HashMap<String,String> map = MySQL.getMap("SELECT `BuildingServerID`,`ToReview` FROM `BuildingServers` WHERE `ToReview` > 0 AND `Maintenance` = 0", "BuildingServerID", "ToReview");

        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("PROJECTS TO REVIEW:");
        e.setTimestamp(new Date().toInstant());
        e.setFooter("IP: BuildTheEarth.net");

        int total = 0;
        String reviewText = "";
        for(String id : map.keySet()){
            reviewText += "**" + id + ":** " + map.get(id) +" Reviews\n";
            total += Integer.parseInt(map.get(id));
        }
        e.addField(new MessageEmbed.Field("", reviewText, false));
        e.addField(new MessageEmbed.Field("", "`Total: " + total +" Reviews`", false));

        event.replyEmbeds(e.build()).submit();
    }

    @Override
    public void configureData(SlashCommandData data) {

    }
}
