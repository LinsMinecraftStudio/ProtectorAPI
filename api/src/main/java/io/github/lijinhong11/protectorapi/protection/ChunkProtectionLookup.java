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

import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Complete block lookup for adapters whose protection data is stored in live blocks.
 */
@ApiStatus.Internal
public final class ChunkProtectionLookup {
    private ChunkProtectionLookup() {}

    /**
     * Check every block in a chunk, stopping at the first protected location.
     * This loads the chunk and covers its full build height, including negative Y coordinates.
     * The predicate may also access neighboring chunks when resolving connected protections.
     *
     * @param world the chunk's world
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @param isProtected the location-based protection lookup
     * @return true if any block in the chunk is protected
     * @throws IllegalStateException if called off the server thread
     */
    public static boolean anyBlock(
            @NotNull World world, int chunkX, int chunkZ, @NotNull Predicate<Location> isProtected) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Live block protection scans must run on the server thread");
        }
        Chunk chunk = world.getChunkAt(chunkX, chunkZ);
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();
        for (int y = minY; y < maxY; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (isProtected.test(chunk.getBlock(x, y, z).getLocation())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
