package io.github.lijinhong11.protectorapi.objects;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A collection to store worlds which the huge protection range lying on
 */
public class WorldCollection {
    private @Nullable World normal = null;
    private @Nullable World nether = null;
    private @Nullable World end = null;

    public WorldCollection(@Nullable World world) {
        if (world == null) {
            return;
        }

        World.Environment env = world.getEnvironment();

        if (env == World.Environment.NORMAL) {
            this.normal = world;
        }

        if (env == World.Environment.NETHER) {
            this.nether = world;
        }

        if (env == World.Environment.THE_END) {
            this.end = world;
        }
    }

    public WorldCollection(@Nullable World normal, @Nullable World nether, @Nullable World end) {
        this.normal = normal;
        this.nether = nether;
        this.end = end;
    }

    public @Nullable World getWorld(@NotNull World.Environment environment) {
        if (environment == World.Environment.NORMAL) {
            return normal;
        }

        if (environment == World.Environment.NETHER) {
            return nether;
        }

        if (environment == World.Environment.THE_END) {
            return end;
        }

        return normal;
    }

    public @Nullable World getFirstAvailableWorld() {
        if (normal != null) {
            return normal;
        }

        if (nether != null) {
            return nether;
        }

        if (end != null) {
            return end;
        }

        return null;
    }

    public @Nullable World getNormalWorld() {
        return normal;
    }

    public @Nullable World getNetherWorld() {
        return nether;
    }

    public @Nullable World getEndWorld() {
        return end;
    }
}
