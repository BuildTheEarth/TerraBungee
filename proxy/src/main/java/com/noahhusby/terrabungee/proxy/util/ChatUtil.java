/*
 * Copyright (c) 2021 Noah Husby
 * TerraBungeeProxy - ChatUtil.java
 */

package com.noahhusby.terrabungee.proxy.util;

import com.noahhusby.terrabungee.proxy.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Noah Husby
 */
public class ChatUtil {
    public static TextComponent title() {
        return new TextComponent(Constants.prefix.replace("&","\u00A7"));
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
        for(Object o : objects) {
            if(o instanceof TextComponent) {
                if(builder != null) {
                    textComponent.addExtra(new TextComponent(builder.toString()));
                    builder = null;
                }

                TextComponent component = (TextComponent) o;
                if(component.getColor() == null && lastFormat != null)
                    component.setColor(lastFormat);

                textComponent.addExtra(component);
            } else {
                if(o instanceof ChatColor)
                    lastFormat = (ChatColor) o;
                if(builder == null) builder = new StringBuilder();
                builder.append(o);
            }
        }

        if(builder != null)
            textComponent.addExtra(new TextComponent(builder.toString()));
        return textComponent;
    }

    public static TextComponent getNoPermission() {
        return combine(ChatColor.RED, "You do not have permission to use this command");
    }

    public static TextComponent getPlayerOnly() {
        return titleAndCombine(ChatColor.RED, "This command can only be executed by players!");
    }
}
