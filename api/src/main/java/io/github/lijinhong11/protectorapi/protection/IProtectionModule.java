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

import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Region and chunk protection. Async methods dispatch to the thread supported by each operation.
 * Completion may occur on either thread; never block the server thread waiting for the result.
 * Returned ranges retain their underlying plugin's thread-safety restrictions.
 */
public interface IProtectionModule {
    /**
     * Get the name of the corresponding plugin
     *
     * @return the name of the corresponding plugin
     */
    @NotNull
    String getPluginName();

    /**
     * Whether the complete adapter call for this operation is safe off the server thread.
     * Defaults to false until the underlying plugin API and this adapter have been audited.
     * This method itself must be thread-safe. Player location capture and global flag reads
     * are dispatched separately; this does not make arbitrary range methods thread-safe.
     *
     * @param check the operation being scheduled
     * @return true only if this operation supports concurrent asynchronous calls
     */
    default boolean supportsAsync(@NotNull ProtectionCheck check) {
        return false;
    }

    /**
     * Check whether any protection intersects the chunk, without loading it.
     * Every region module must implement this lookup using its protection index or region bounds.
     * The lookup must cover the entire chunk, including partially intersecting regions,
     * and must not approximate region protection by sampling locations.
     *
     * @param world the chunk's world
     * @param chunkX the chunk X coordinate
     * @param chunkZ the chunk Z coordinate
     * @return true if the chunk is protected
     */
    boolean isProtected(@NotNull World world, int chunkX, int chunkZ);

    /**
     * Check an already obtained chunk without loading it.
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
     * Dispatch a region lookup using a copy of the supplied location.
     *
     * @param location the location
     * @return a future containing true if the location is in the protection range
     * @see #isInProtectionRange(Location)
     */
    default CompletableFuture<Boolean> isInProtectionRangeAsync(@NotNull Location location) {
        Location snapshot = location.clone();
        return schedule(supportsAsync(ProtectionCheck.LOOKUP), () -> isInProtectionRange(snapshot));
    }

    /**
     * Capture the player's location on the server thread before dispatching lookup.
     *
     * @param player the player
     * @return a future containing true if the player is in the protection range
     * @see #isInProtectionRangeAsync(Location)
     */
    default CompletableFuture<Boolean> isInProtectionRangeAsync(@NotNull Player player) {
        return schedule(false, player::getLocation).thenCompose(this::isInProtectionRangeAsync);
    }

    /**
     * Dispatch range retrieval using a copy of the supplied location.
     *
     * @param location the location
     * @return a future containing the protection range info, or null if the location is not protected
     * @see #getProtectionRangeInfo(Location)
     */
    default CompletableFuture<IProtectionRange> getProtectionRangeInfoAsync(@NotNull Location location) {
        Location snapshot = location.clone();
        return schedule(supportsAsync(ProtectionCheck.LOOKUP), () -> getProtectionRangeInfo(snapshot));
    }

    /**
     * Capture the player's location on the server thread before retrieving the range.
     *
     * @param player the player
     * @return a future containing the protection range info, or null if the player is not in a protection range
     * @see #getProtectionRangeInfoAsync(Location)
     */
    default CompletableFuture<IProtectionRange> getProtectionRangeInfoAsync(@NotNull Player player) {
        return schedule(false, player::getLocation).thenCompose(this::getProtectionRangeInfoAsync);
    }

    /**
     * Dispatch owned-range enumeration independently of location lookup capability.
     *
     * @param player the player
     * @return a future containing all protection range info that the player owns
     * @see #getProtectionRangeInfos(OfflinePlayer)
     */
    default CompletableFuture<List<? extends IProtectionRange>> getProtectionRangeInfosAsync(
            @NotNull OfflinePlayer player) {
        return schedule(supportsAsync(ProtectionCheck.OWNED_RANGES), () -> getProtectionRangeInfos(player));
    }

    /**
     * Dispatch a global flag read; flag mutations remain subject to the plugin's threading rules.
     *
     * @param flag the flag
     * @param world the world
     * @return a future containing the global flag, completing exceptionally if global flags are unsupported
     * @see #getGlobalFlag(String, String)
     */
    default CompletableFuture<FlagState<?>> getGlobalFlagAsync(@NotNull String flag, @NotNull String world) {
        return schedule(supportsAsync(ProtectionCheck.GLOBAL_FLAGS), () -> getGlobalFlag(flag, world));
    }

    /**
     * Dispatch a global flag read according to {@link ProtectionCheck#GLOBAL_FLAGS}.
     *
     * @param flag the flag
     * @param world the world
     * @return a future containing the global flag, completing exceptionally if global flags are unsupported
     * @see #getGlobalFlag(CommonFlags, String)
     */
    default CompletableFuture<FlagState<?>> getGlobalFlagAsync(@NotNull CommonFlags flag, @NotNull String world) {
        return schedule(supportsAsync(ProtectionCheck.GLOBAL_FLAGS), () -> getGlobalFlag(flag, world));
    }

    /**
     * Check if the player is in the protection range
     *
     * @param player the player
     * @return true if the player is in the protection range
     */
    default boolean isInProtectionRange(@NotNull Player player) {
        return isInProtectionRange(player.getLocation());
    }

    /**
     * Get the protection range info of the player's location
     *
     * @param player the player
     * @return the protection range info
     */
    default @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Player player) {
        return getProtectionRangeInfo(player.getLocation());
    }

    /**
     * Get all protection range info that the player owns
     *
     * @param player the player
     * @return the protection range info
     */
    List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player);

    /**
     * Check if the location is in the protection range
     *
     * @param location the location
     * @return true if the location is in the protection range
     */
    boolean isInProtectionRange(@NotNull Location location);

    /**
     * Get the protection range info of the location
     *
     * @param location the location
     * @return the protection range info (if the location is not in the protection range, return null)
     */
    @Nullable
    IProtectionRange getProtectionRangeInfo(@NotNull Location location);

    /**
     * Whether the module supports global flags
     *
     * @return true if the module supports global flags
     */
    boolean isSupportGlobalFlags();

    /**
     * Get the global flag
     *
     * @param flag  the flag
     * @param world the world
     * @return the global flag
     * @throws UnsupportedOperationException if the module does not support global flags
     */
    FlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world);

    /**
     * Get the global flag
     *
     * @param flag  the flag
     * @param world the world
     * @return the global flag
     * @throws UnsupportedOperationException if the module does not support global flags
     */
    FlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world);

    /**
     * Set the global flag
     *
     * @param flag the flag
     * @throws UnsupportedOperationException if the module does not support global flags
     */
    void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value);

    /**
     * Set the global flag
     *
     * @param world the world
     * @param flag  the flag
     * @throws UnsupportedOperationException if the module does not support global flags
     */
    void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value);
}
