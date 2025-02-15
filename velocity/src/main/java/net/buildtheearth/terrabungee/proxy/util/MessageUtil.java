package net.buildtheearth.terrabungee.proxy.util;

import net.buildtheearth.terrabungee.proxy.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {

    
    public static Component PREFIX = LegacyComponentSerializer.legacyAmpersand().deserialize(Constants.prefix);

    public static Component NO_PERMISSION = Component.text()
                    .append(PREFIX)
                    .append(Component.text("You do not have permission to use this command", NamedTextColor.RED))
                    .build();


    public static Component NO_CONTACT = Component.text()
                    .append(PREFIX)
                    .append(Component.text("Unable to contact the controller! Please try again", NamedTextColor.RED))
                    .build();

    public static Component PLAYER_ONLY = Component.text()
                    .append(PREFIX)
                    .append(Component.text("This command can only be executed by players!", NamedTextColor.RED))
                    .build();



    public static Component USAGE(String command) {
        return Component.text()
                    .append(PREFIX)
                    .append(Component.text("Usage: /" + command, NamedTextColor.RED))
                    .build();
    }
}
