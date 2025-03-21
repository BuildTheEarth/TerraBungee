/*
 * Copyright (c) 2025 BuildTheEarth
 * TerraBungeeProxy - ChatUtil.java
 */

package net.buildtheearth.terrabungee.proxy.util;

import com.velocitypowered.api.command.CommandSource;
import net.buildtheearth.terrabungee.proxy.Constants;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Arrays;

/**
 * @author Noah Husby, MineFact
 */
@UtilityClass
public class ChatUtil {

    // ======== PREDEFINED MESSAGES ========

    public static TextComponent PREFIX = LegacyComponentSerializer.legacyAmpersand().deserialize(Constants.prefix);
    public static TextComponent EMPTY_TEXT = Component.text("");

    public static TextComponent PLAYER_ONLY = titleAndCombineError("This command can only be executed by players!");
    public static TextComponent NO_CONTROLLER_CONTACT = titleAndCombineError("Unable to contact the controller! Please try again");
    public static TextComponent NO_PERMISSION = titleAndCombineError("You do not have permission to use this command");



    // ======== PREDEFINED MESSAGES WITH PARAMETERS ========

    public static TextComponent USAGE(String command) {
        return titleAndCombineError("Usage: /" + command);
    }

    public static TextComponent NOT_A_NUMBER(String number) {
        return titleAndCombineError(number, " is not a valid number");
    }

    public static TextComponent PLAYER_NEVER_JOINED(String playerName) {
        return titleAndCombine(NamedTextColor.YELLOW, playerName, NamedTextColor.GRAY, " has never joined the network!");
    }

    public static TextComponent PLAYER_NOT_ONLINE(String playerName) {
        return titleAndCombine(NamedTextColor.YELLOW, playerName, NamedTextColor.GRAY, " is not online!");
    }

    public static TextComponent PLAYER_HAS_NO_PUNISHMENTS(String playerName) {
        return titleAndCombine(NamedTextColor.YELLOW, playerName, NamedTextColor.GRAY, " has no punishments on record!");
    }



    // ======== HELPER METHODS ========

    /**
     * A message that is combined by the given objects with the global prefix.
     *
     * @param objects the objects to combine. Possible types: {@link TextComponent}, {@link String}, {@link TextColor}, {@link TextDecoration}
     * @return prefix followed by the given objects
     */
    public static TextComponent titleAndCombine(Object... objects) {
        return combine(true, objects);
    }

    /**
     * A red error message that is combined by the given objects with the global prefix.
     *
     * @param objects the objects to combine. Possible types: {@link TextComponent}, {@link TextColor}, {@link TextDecoration}
     * @return prefix followed by the given objects in red if not colored otherwise
     */
    public static TextComponent titleAndCombineError(Object... objects) {
        Object[] combinedObjects = new Object[objects.length + 1];
        combinedObjects[0] = NamedTextColor.RED;
        System.arraycopy(objects, 0, combinedObjects, 1, objects.length);
        return titleAndCombine(combinedObjects);
    }

    /**
     * A message that is combined by the given objects.
     *
     * @param objects the objects to combine. Possible types: {@link TextComponent}, {@link String}, {@link TextColor}, {@link TextDecoration}
     * @return the given objects combined
     */
    public static TextComponent combine(Object... objects) {
        return combine(false, objects);
    }

    /**
     * A message that is combined by the given objects.
     *
     * @param title if true, the message will be combined with the global prefix
     * @param objects the objects to combine. Possible types: {@link TextComponent}, {@link String}, {@link TextColor}, {@link TextDecoration}
     * @return the given objects combined
     */
    public static TextComponent combine(boolean title, Object... objects) {
        TextComponent textComponent = title ? PREFIX : Component.text().build();
        StringBuilder builder = null;
        TextColor lastFormat = null;
        TextDecoration lastDecoration = null;
        for (Object o : objects) {
            if (o instanceof TextComponent component) {
                if (builder != null) {
                    component = component.append(Component.text(builder.toString(), lastFormat));

                    if(lastDecoration != null)
                        component = component.decorate(lastDecoration);

                    builder = null;
                }

                textComponent = textComponent.append(component);
            } else if (o instanceof NamedTextColor) {
                lastFormat = (NamedTextColor) o;
            } else if (o instanceof TextDecoration) {
                lastDecoration = (TextDecoration) o;
            } else {
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(o);

                TextComponent component = Component.text(o.toString(), lastFormat);
                if(lastDecoration != null)
                    component = component.decorate(lastDecoration);
                textComponent = textComponent.append(component);
            }
        }

        return textComponent;
    }

    /**
     * Sends a message box with the given title and text to the player
     *
     * @param sender The player to send the message to
     * @param title The title of the message
     * @param text The text of the message
     */
    public static void sendMessageBox(CommandSource sender, String title, Component text) {
        sendMessageBox(sender, title, () -> sender.sendMessage(text));
    }

    /**
     * Sends a message box with the given title and text to the player.
     * With the given runnable multiple messages can be sent inside the message box.
     *
     * @param sender The player to send the message to
     * @param title The title of the message
     * @param runnable The runnable that sends the messages inside the message box
     */
    public static void sendMessageBox(CommandSource sender, String title, Runnable runnable) {
        sender.sendMessage(combine(NamedTextColor.DARK_GRAY + "" + TextDecoration.STRIKETHROUGH, "==============", NamedTextColor.DARK_GRAY, " " + TextDecoration.BOLD + title + " ", NamedTextColor.DARK_GRAY + "" + TextDecoration.STRIKETHROUGH, "=============="));
        sender.sendMessage(Component.text().build());

        runnable.run();

        // Strips the color from the title
        int length = PlainTextComponentSerializer.plainText().serialize(LegacyComponentSerializer.legacySection().deserialize(title)).length();
        char[] array = new char[length];
        Arrays.fill(array, '=');
        String bottom = "==============================" + new String(array);
        sender.sendMessage(Component.text().build());
        sender.sendMessage(combine(NamedTextColor.DARK_GRAY + "" + TextDecoration.STRIKETHROUGH, bottom));
    }
}
