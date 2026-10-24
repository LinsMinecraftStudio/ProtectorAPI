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

import static io.github.lijinhong11.protectorapi.ProtectionCheckScheduler.schedule;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Provides protection lookups and player permission checks for individual blocks.
 * Synchronous calls execute on the caller's thread; asynchronous support is declared per operation.
 * Async methods dispatch each operation to its supported thread and complete exceptionally on failure.
 * Completion may occur on either thread; never block the server thread waiting for the result.
 * The synchronous Block overloads read the block's location and should be called on the server thread.
 */
public interface IBlockProtectionModule {
    /**
     * Get the name of the corresponding plugin.
     *
     * @return the name of the corresponding plugin
     */
    @NotNull
    String getPluginName();

    /**
     * Whether the complete location-based lookup or permission check is thread-safe.
     * Defaults to false: accessing Bukkit blocks, chunks or player state may require the
     * server thread even when the underlying plugin supports some asynchronous APIs.
     * This method itself must be thread-safe. GLOBAL_FLAGS is unused for block modules.
     *
     * @param check the operation being scheduled
     * @return true only if this operation supports concurrent asynchronous calls
     */
    default boolean supportsAsync(@NotNull ProtectionCheck check) {
        return false;
    }

    /**
     * Check whether any block protection exists in the chunk.
     * Every block module must implement this lookup using its protection index or a complete
     * block scan. Implementations that need live block state may load the chunk and must run
     * on the server thread. A lookup must not report an unloaded chunk as unprotected merely
     * because it has not been loaded.
     *
     * @param world the chunk's world
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @return true if the chunk is protected
     */
    boolean isProtected(@NotNull World world, int chunkX, int chunkZ);

    /**
     * Check an already obtained chunk for block protection.
     *
     * @param chunk the chunk
     * @return true if the chunk is protected
     * @see #isProtected(World, int, int)
     */
    default boolean isProtected(@NotNull Chunk chunk) {
        return isProtected(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Dispatch a chunk-coordinate lookup according to {@link ProtectionCheck#CHUNK_LOOKUP}.
     *
     * @param world the chunk's world
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @return a future containing true if the chunk is protected
     * @see #isProtected(World, int, int)
     */
    default CompletableFuture<Boolean> isProtectedAsync(@NotNull World world, int chunkX, int chunkZ) {
        return schedule(supportsAsync(ProtectionCheck.CHUNK_LOOKUP), () -> isProtected(world, chunkX, chunkZ));
    }

    /**
     * Capture the supplied chunk's coordinates on the server thread before dispatching lookup.
     *
     * @param chunk the chunk
     * @return a future containing true if the chunk is protected
     * @see #isProtectedAsync(World, int, int)
     */
    default CompletableFuture<Boolean> isProtectedAsync(@NotNull Chunk chunk) {
        return schedule(false, () -> isProtectedAsync(chunk.getWorld(), chunk.getX(), chunk.getZ()))
                .thenCompose(future -> future);
    }

    /**
     * Dispatch a lookup using a copy of the supplied location.
     *
     * @param block the block location
     * @return a future containing true if the block is protected
     * @see #isProtected(Location)
     */
    default CompletableFuture<Boolean> isProtectedAsync(@NotNull Location block) {
        Location snapshot = block.clone();
        return schedule(supportsAsync(ProtectionCheck.LOOKUP), () -> isProtected(snapshot));
    }

    /**
     * Dispatch a break permission check using a copy of the supplied location.
     *
     * @param player the player
     * @param block the block location
     * @return a future containing true if the player can break the block
     * @see #allowBreak(Player, Location)
     */
    default CompletableFuture<Boolean> allowBreakAsync(@NotNull Player player, @NotNull Location block) {
        Location snapshot = block.clone();
        return schedule(supportsAsync(ProtectionCheck.FLAGS), () -> allowBreak(player, snapshot));
    }

    /**
     * Dispatch a place permission check using a copy of the supplied location.
     *
     * @param player the player
     * @param block the block location
     * @return a future containing true if the player can place a block at the location
     * @see #allowPlace(Player, Location)
     */
    default CompletableFuture<Boolean> allowPlaceAsync(@NotNull Player player, @NotNull Location block) {
        Location snapshot = block.clone();
        return schedule(supportsAsync(ProtectionCheck.FLAGS), () -> allowPlace(player, snapshot));
    }

    /**
     * Dispatch an interaction permission check using a copy of the supplied location.
     *
     * @param player the player
     * @param block the block location
     * @return a future containing true if the player can interact with the block
     * @see #allowInteract(Player, Location)
     */
    default CompletableFuture<Boolean> allowInteractAsync(@NotNull Player player, @NotNull Location block) {
        Location snapshot = block.clone();
        return schedule(supportsAsync(ProtectionCheck.FLAGS), () -> allowInteract(player, snapshot));
    }

    /**
     * Capture the block location on the server thread before dispatching lookup.
     *
     * @param block the block
     * @return a future containing true if the block is protected
     * @see #isProtectedAsync(Location)
     */
    default CompletableFuture<Boolean> isProtectedAsync(@NotNull Block block) {
        return schedule(false, block::getLocation).thenCompose(this::isProtectedAsync);
    }

    /**
     * Capture the block location on the server thread before checking break permission.
     *
     * @param player the player
     * @param block the block
     * @return a future containing true if the player can break the block
     * @see #allowBreakAsync(Player, Location)
     */
    default CompletableFuture<Boolean> allowBreakAsync(@NotNull Player player, @NotNull Block block) {
        return schedule(false, block::getLocation).thenCompose(location -> allowBreakAsync(player, location));
    }

    /**
     * Capture the block location on the server thread before checking place permission.
     *
     * @param player the player
     * @param block the block at the target location
     * @return a future containing true if the player can place a block at the location
     * @see #allowPlaceAsync(Player, Location)
     */
    default CompletableFuture<Boolean> allowPlaceAsync(@NotNull Player player, @NotNull Block block) {
        return schedule(false, block::getLocation).thenCompose(location -> allowPlaceAsync(player, location));
    }

    /**
     * Capture the block location on the server thread before checking interaction permission.
     *
     * @param player the player
     * @param block the block
     * @return a future containing true if the player can interact with the block
     * @see #allowInteractAsync(Player, Location)
     */
    default CompletableFuture<Boolean> allowInteractAsync(@NotNull Player player, @NotNull Block block) {
        return schedule(false, block::getLocation).thenCompose(location -> allowInteractAsync(player, location));
    }

    /**
     * Check if the block at the location is protected by this module.
     *
     * @param block the block location
     * @return true if the block is protected
     */
    boolean isProtected(Location block);

    /**
     * Check if the player can break a block at the location.
     *
     * @param player the player
     * @param block the block location
     * @return true if the player can break the block
     */
    boolean allowBreak(Player player, Location block);

    /**
     * Check if the player can place a block at the location.
     *
     * @param player the player
     * @param block the block location
     * @return true if the player can place a block
     */
    boolean allowPlace(Player player, Location block);

    /**
     * Check if the player can interact with the block at the location.
     *
     * @param player the player
     * @param block the block location
     * @return true if the player can interact with the block
     */
    boolean allowInteract(Player player, Location block);

    /**
     * Check if the block is protected by this module.
     *
     * @param block the block
     * @return true if the block is protected
     * @see #isProtected(Location)
     */
    default boolean isProtected(Block block) {
        return isProtected(block.getLocation());
    }

    /**
     * Check if the player can break the block.
     *
     * @param player the player
     * @param block the block
     * @return true if the player can break the block
     * @see #allowBreak(Player, Location)
     */
    default boolean allowBreak(Player player, Block block) {
        return allowBreak(player, block.getLocation());
    }

    /**
     * Check if the player can place a block at the supplied block's location.
     *
     * @param player the player
     * @param block the block at the target location
     * @return true if the player can place a block
     * @see #allowPlace(Player, Location)
     */
    default boolean allowPlace(Player player, Block block) {
        return allowPlace(player, block.getLocation());
    }

    /**
     * Check if the player can interact with the block.
     *
     * @param player the player
     * @param block the block
     * @return true if the player can interact with the block
     * @see #allowInteract(Player, Location)
     */
    default boolean allowInteract(Player player, Block block) {
        return allowInteract(player, block.getLocation());
    }
}
