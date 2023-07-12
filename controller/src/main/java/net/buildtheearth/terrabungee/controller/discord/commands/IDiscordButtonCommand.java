package net.buildtheearth.terrabungee.controller.discord.commands;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author Noah Husby
 */
public interface IDiscordButtonCommand {
    void onButtonEvent(JsonObject data, ButtonInteractionEvent event);
}
