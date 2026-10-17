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
package io.github.lijinhong11.protectorapi.protection;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Provides protection lookups at chunk granularity.
 *
 * <p>The coordinate overload is the primary API. Implementations must not load a chunk while
 * answering it. Modules that only expose block or region lookups should not implement this
 * interface.
 */
public interface IChunkProtectionModule {
    /**
     * Get the name of the corresponding plugin.
     *
     * @return the name of the corresponding plugin
     */
    @NotNull
    String getPluginName();

    /**
     * Whether the complete chunk lookup is safe off the server thread.
     *
     * @param check the operation being scheduled
     * @return true only if this operation supports concurrent asynchronous calls
     */
    default boolean supportsAsync(@NotNull ProtectionCheck check) {
        return false;
    }

    /**
     * Check whether the chunk is protected.
     *
     * @param world the chunk's world
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @return true if the chunk is protected
     */
    boolean isProtected(@NotNull World world, int chunkX, int chunkZ);

    /**
     * Check whether the supplied, already obtained chunk is protected.
     *
     * @param chunk the chunk
     * @return true if the chunk is protected
     * @see #isProtected(World, int, int)
     */
    default boolean isProtected(@NotNull Chunk chunk) {
        return isProtected(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }
}
