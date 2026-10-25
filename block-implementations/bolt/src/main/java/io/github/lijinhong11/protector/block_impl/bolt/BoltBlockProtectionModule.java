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
package io.github.lijinhong11.protector.block_impl.bolt;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.popcraft.bolt.BoltAPI;
import org.popcraft.bolt.util.Permission;

public class BoltBlockProtectionModule implements IBlockProtectionModule {
    private final BoltAPI api;

    public BoltBlockProtectionModule() {
        api = Bukkit.getServer().getServicesManager().load(BoltAPI.class);
    }

    @Override
    public @NotNull String getPluginName() {
        return "Bolt";
    }

    @Override
    public boolean isProtected(@NotNull World world, int chunkX, int chunkZ) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        BoundingBox bounds =
                new BoundingBox(minX, world.getMinHeight(), minZ, minX + 16, world.getMaxHeight(), minZ + 16);
        return !api.findProtections(world, bounds).isEmpty();
    }

    @Override
    public boolean isProtected(Location block) {
        return api.isProtected(block.getBlock());
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return !isProtected(block) || api.canAccess(block.getBlock(), player, Permission.DESTROY);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return !isProtected(block) || api.canAccess(block.getBlock(), player, Permission.INTERACT);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return !isProtected(block) || api.canAccess(block.getBlock(), player, Permission.INTERACT);
    }
}
