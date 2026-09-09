package net.buildtheearth.terrabungee.proxy.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UuidAllowlistTest {
    private static final UUID ADMIN = UUID.fromString("ac10ddeb-c65a-408e-a036-f7a7e51254f4");
    private static final UUID MODERATOR = UUID.fromString("81f2eb9f-6ac0-487f-8fc4-c46037542ecf");

    @Test
    void administratorsInheritModerationButModeratorsDoNotInheritAdministration() {
        UuidAllowlist allowlist = new UuidAllowlist(
                java.util.Set.of(ADMIN),
                java.util.Set.of(MODERATOR)
        );

        assertTrue(allowlist.isAdmin(ADMIN));
        assertTrue(allowlist.isModerator(ADMIN));
        assertTrue(allowlist.isModerator(MODERATOR));
        assertFalse(allowlist.isAdmin(MODERATOR));
    }

    @Test
    void unknownAndNullUuidsAreDenied() {
        UuidAllowlist allowlist = new UuidAllowlist(
                java.util.Set.of(ADMIN),
                java.util.Set.of(MODERATOR)
        );

        assertFalse(allowlist.isAdmin(UUID.randomUUID()));
        assertFalse(allowlist.isModerator(UUID.randomUUID()));
        assertFalse(allowlist.isAdmin(null));
        assertFalse(allowlist.isModerator(null));
    }

    @Test
    void invalidEntriesAreSkippedWithoutDiscardingValidEntries() {
        List<String> warnings = new ArrayList<>();
        UuidAllowlist allowlist = UuidAllowlist.parse(
                new String[]{ADMIN.toString(), "not-a-uuid"},
                new String[]{" " + MODERATOR + " "},
                warnings::add
        );

        assertTrue(allowlist.isAdmin(ADMIN));
        assertTrue(allowlist.isModerator(MODERATOR));
        assertFalse(warnings.isEmpty());
    }
}
