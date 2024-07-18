/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - ChatUtil.java
 */

package net.buildtheearth.terrabungee.proxy.util;

import com.noahhusby.terrabungee.proxy.Constants;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;

/**
 * @author Noah Husby
 */
@UtilityClass
public class ChatUtil {
    public static TextComponent title() {
        return new TextComponent(Constants.prefix.replace("&", "\u00A7"));
    }

    public static TextComponent titleAndCombine(Object... objects) {
        return combine(true, objects);
    }

    public static TextComponent combine(Object... objects) {
        return combine(false, objects);
    }

    public static TextComponent combine(boolean title, Object... objects) {
        TextComponent textComponent = title ? title() : new TextComponent("");
        StringBuilder builder = null;
        ChatColor lastFormat = null;
        for (Object o : objects) {
            if (o instanceof TextComponent) {
                if (builder != null) {
                    textComponent.addExtra(new TextComponent(builder.toString()));
                    builder = null;
                }

                TextComponent component = (TextComponent) o;
                if (component.getColor() == null && lastFormat != null) {
                    component.setColor(lastFormat);
                }

                textComponent.addExtra(component);
            } else {
                if (o instanceof ChatColor) {
                    lastFormat = (ChatColor) o;
                }
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(o);
            }
        }

        if (builder != null) {
            textComponent.addExtra(new TextComponent(builder.toString()));
        }
        return textComponent;
    }

    public static void sendMessageBox(CommandSender sender, String title, BaseComponent text) {
        sendMessageBox(sender, title, () -> sender.sendMessage(text));
    }

    public static void sendMessageBox(CommandSender sender, String title, Runnable runnable) {
        sender.sendMessage(combine(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH, "==============", ChatColor.RESET, " " + ChatColor.BOLD + title + " ", ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH, "=============="));
        sender.sendMessage();

        runnable.run();

        int length = ChatColor.stripColor(title).length();
        char[] array = new char[length];
        Arrays.fill(array, '=');
        String bottom = "==============================" + new String(array);
        sender.sendMessage();
        sender.sendMessage(combine(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH, bottom));
    }

    public static TextComponent getNoPermission() {
        return combine(ChatColor.RED, "You do not have permission to use this command");
    }

    public static TextComponent getNoContact() {
        return titleAndCombine(ChatColor.RED, "Unable to contact the controller! Please try again.");
    }

    public static TextComponent getPlayerOnly() {
        return titleAndCombine(ChatColor.RED, "This command can only be executed by players!");
    }
}
