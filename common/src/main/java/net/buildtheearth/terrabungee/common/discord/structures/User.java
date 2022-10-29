package net.buildtheearth.terrabungee.common.discord.structures;

import lombok.Getter;

import java.util.List;

/**
 * @author Xbox Bedrock
 */
public class User {
    @Getter
    private List<Role> roles;

    @Getter
    private String id;

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
}
