package net.buildtheearth.terrabungee.proxy.config;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Immutable UUID-based authorization lists for player-issued TerraBungee commands.
 */
public record UuidAllowlist(Set<UUID> admins, Set<UUID> moderators) {

    public UuidAllowlist {
        admins = Set.copyOf(admins);
        moderators = Set.copyOf(moderators);
    }

    public static UuidAllowlist empty() {
        return new UuidAllowlist(Set.of(), Set.of());
    }

    public static UuidAllowlist parse(String[] admins, String[] moderators, Consumer<String> warningLogger) {
        return new UuidAllowlist(
                parseList("Admin UUIDs", admins, warningLogger),
                parseList("Moderator UUIDs", moderators, warningLogger)
        );
    }

    private static Set<UUID> parseList(String name, String[] entries, Consumer<String> warningLogger) {
        Set<UUID> parsed = new LinkedHashSet<>();
        if (entries == null) {
            return parsed;
        }
        for (String entry : entries) {
            try {
                if (entry == null) {
                    throw new IllegalArgumentException("UUID entry is null");
                }
                parsed.add(UUID.fromString(entry.trim()));
            } catch (RuntimeException exception) {
                warningLogger.accept("Ignoring invalid entry in " + name + ": " + entry);
            }
        }
        return parsed;
    }

    public boolean isAdmin(UUID uuid) {
        return uuid != null && admins.contains(uuid);
    }

    public boolean isModerator(UUID uuid) {
        return uuid != null && (isAdmin(uuid) || moderators.contains(uuid));
    }
}
