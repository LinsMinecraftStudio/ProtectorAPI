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
package io.github.lijinhong11.protector.block_impl.griefprevention;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IChunkProtectionModule;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GriefPreventionBlockProtectionModule implements IBlockProtectionModule, IChunkProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "GriefPrevention";
    }

    @Override
    public boolean supportsAsync(@NotNull ProtectionCheck check) {
        return check == ProtectionCheck.CHUNK_LOOKUP;
    }

    @Override
    public boolean isProtected(@NotNull World world, int chunkX, int chunkZ) {
        if (!GriefPrevention.instance.claimsEnabledForWorld(world)) {
            return false;
        }
        for (Claim claim : GriefPrevention.instance.dataStore.getClaims(chunkX, chunkZ)) {
            if (world.equals(claim.getGreaterBoundaryCorner().getWorld())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isProtected(Location block) {
        return getClaim(block) != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        Claim c = getClaim(block);
        return c == null || c.hasExplicitPermission(player, ClaimPermission.Build);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return allowBreak(player, block);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        Claim c = getClaim(block);
        return c == null || c.hasExplicitPermission(player, ClaimPermission.Inventory);
    }

    private Claim getClaim(Location loc) {
        if (!GriefPrevention.instance.claimsEnabledForWorld(loc.getWorld())) {
            return null;
        }

        return GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
    }
}
