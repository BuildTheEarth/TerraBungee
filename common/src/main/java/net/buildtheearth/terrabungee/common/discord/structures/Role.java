package net.buildtheearth.terrabungee.common.discord.structures;

import lombok.Getter;

import java.util.Objects;

/**
 * @author Xbox Bedrock
 */
public class Role {
    @Getter
    private final String name;

    @Getter
    private final String id;

    public Role(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object b) {
        if (b == this) return true;
        if (!(b instanceof Role)) return false;
        Role cast = (Role) b;
        return Objects.equals(this.id, cast.id);
    }
}
