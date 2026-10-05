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

public interface IBlockProtectionModule {
    @NotNull
    String getPluginName();

    boolean isProtected(Location block);

    boolean allowBreak(Player player, Location block);

    boolean allowPlace(Player player, Location block);

    boolean allowInteract(Player player, Location block);

    default boolean isProtected(Block block) {
        return isProtected(block.getLocation());
    }

    default boolean allowBreak(Player player, Block block) {
        return allowBreak(player, block.getLocation());
    }

    default boolean allowPlace(Player player, Block block) {
        return allowPlace(player, block.getLocation());
    }

    default boolean allowInteract(Player player, Block block) {
        return allowInteract(player, block.getLocation());
    }
}
