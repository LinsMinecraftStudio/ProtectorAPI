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

import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IProtectionModule {
    /**
     * Get the name of the corresponding plugin
     *
     * @return the name of the corresponding plugin
     */
    @NotNull
    String getPluginName();

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
