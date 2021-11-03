package net.buildtheearth.terrabungee.common;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.buildtheearth.terrabungee.common.exceptions.VersionParseException;

import java.util.Objects;

/**
 * @author Noah Husby
 * A class represtenting a single version of TerraBungee
 */
@Getter
@RequiredArgsConstructor
public class TerraBungeeVersion implements Comparable<TerraBungeeVersion> {
    private final int majorVersion;
    private final int minorVersion;
    private final int buildVersion;

    private final boolean isDevBuild;

    public TerraBungeeVersion(int major, int minor, int build) {
        this(major, minor, build, false);
    }

    public TerraBungeeVersion(String version) throws VersionParseException {
        if(version == null) {
            majorVersion = minorVersion = buildVersion = 0;
            isDevBuild = true;
            return;
        }
        String[] versions = version.split("\\.");
        if(versions.length < 3) {
            throw new VersionParseException(String.format("Invalid version input: %s", version));
        }
        try {
            majorVersion = Integer.parseInt(versions[0]);
            minorVersion = Integer.parseInt(versions[1]);
            buildVersion = Integer.parseInt(versions[2]);
            isDevBuild = false;
        } catch (NumberFormatException e) {
            throw new VersionParseException(String.format("Invalid version input: %s", version));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, buildVersion);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TerraBungeeVersion)) {
            return false;
        }
        return this.compareTo((TerraBungeeVersion) other) == 0;
    }

    @Override
    public int compareTo(TerraBungeeVersion other) {
        if(other == null) {
            return Integer.MAX_VALUE;
        }

        if(this.isDevBuild() && other.isDevBuild()) {
            return 0;
        } else if(this.isDevBuild) {
            return Integer.MAX_VALUE;
        } else if(other.isDevBuild) {
            return Integer.MIN_VALUE;
        }

        int majorCompare = this.majorVersion - other.majorVersion;
        if(majorCompare != 0) {
            return majorCompare;
        }

        int minorCompare = this.minorVersion - other.minorVersion;
        if(minorCompare != 0) {
            return minorCompare;
        }

        return this.buildVersion - other.buildVersion;
    }

    @Override
    public String toString() {
        String version;
        if(isDevBuild) {
            version = "[Development Build]";
        } else {
            version = String.format("%d.%d.%d", majorVersion, minorVersion, buildVersion);
        }
        return version;
    }

    /**
     * Checks whether another version is newer than the local version
     *
     * @param other {@link TerraBungeeVersion}
     * @return True if compared version is newer, false if not
     */
    public boolean isNewer(TerraBungeeVersion other) {
        return this.compareTo(other) > 0;
    }

    /**
     * Checks whether another version is older than the local version
     *
     * @param other {@link TerraBungeeVersion}
     * @return True if compared version is older, false if not
     */
    public boolean isOlder(TerraBungeeVersion other) {
        return this.compareTo(other) < 0;
    }

    /**
     * Checks whether another version is the same as the local version
     *
     * @param other {@link TerraBungeeVersion}
     * @return True if compared version is the same, false if not
     */
    public boolean isSame(TerraBungeeVersion other) {
        return this.compareTo(other) == 0;
    }
}
