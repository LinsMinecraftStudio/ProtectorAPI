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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Provides protection lookups and player permission checks for individual blocks.
 * Calls execute on the caller's thread; asynchronous support is declared per operation.
 * The Block overloads read the block's location and should be called on the server thread.
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
