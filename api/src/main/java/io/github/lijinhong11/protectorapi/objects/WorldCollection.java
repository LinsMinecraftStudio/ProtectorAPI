/*
 * ProtectorAPI
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
