package net.buildtheearth.terrabungee.common.discord.structures;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author Xbox Bedrock
 */
public class Builder {
    @Accessors(prefix="has") @Getter()
    private boolean hasBuilder;
    @Getter
    private String id;
}
