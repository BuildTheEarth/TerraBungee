package net.buildtheearth.terrabungee.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.buildtheearth.terrabungee.proxy.TerraBungeeProxy;
import net.buildtheearth.terrabungee.proxy.config.ConfigHandler;

/**
 * UUID-based command authorization. Permission plugins are deliberately not consulted.
 */
public final class CommandAuthorization {
    private CommandAuthorization() {
    }

    public static boolean isAdmin(CommandSource source) {
        if (isConsole(source)) {
            return true;
        }
        return source instanceof Player player
                && ConfigHandler.getAuthorization().isAdmin(player.getUniqueId());
    }

    public static boolean isModerator(CommandSource source) {
        if (isConsole(source)) {
            return true;
        }
        return source instanceof Player player
                && ConfigHandler.getAuthorization().isModerator(player.getUniqueId());
    }

    private static boolean isConsole(CommandSource source) {
        return source != null
                && TerraBungeeProxy.getServer() != null
                && TerraBungeeProxy.getServer().getConsoleCommandSource().equals(source);
    }
}
