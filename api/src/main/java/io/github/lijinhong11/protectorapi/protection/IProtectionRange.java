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
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import java.util.List;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IProtectionRange {
    /**
     * Get id.
     * Sometimes it can be an integer, or UUID, or empty (if the module doesn't support)
     *
     * @return id
     */
    @NotNull
    String getId();

    /**
     * Get display name or empty (if the module doesn't support)
     *
     * @return display name
     */
    @NotNull
    String getDisplayName();

    /**
     * Get the world which the range exists
     *
     * @return world
     */
    @NotNull
    WorldCollection getWorld();

    /**
     * Get all flags
     *
     * @return all flags (the map is not modifiable)
     */
    @NotNull
    Map<String, FlagState<?>> getFlags();

    /**
     * Get flag state
     *
     * @param flag flag name
     * @return flag state
     */
    FlagState<?> getFlagState(@NotNull String flag);

    /**
     * Get flag state
     *
     * @param flag   flag name
     * @param player player
     * @return flag state
     */
    FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player);

    /**
     * Get flag state
     *
     * @param flag flag
     * @return flag state
     */
    FlagState<?> getFlagState(@NotNull CommonFlags flag);

    /**
     * Get flag state
     *
     * @param flag   flag
     * @param player player
     * @return flag state
     */
    FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player);

    /**
     * Get all admins
     *
     * @return all admins
     */
    List<OfflinePlayer> getAdmins();

    /**
     * Get all members
     *
     * @return all members
     */
    List<OfflinePlayer> getMembers();

    /**
     * Get owner
     *
     * @return owner
     */
    @Nullable
    OfflinePlayer getOwner();
}
