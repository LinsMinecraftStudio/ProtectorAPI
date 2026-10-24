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

import static io.github.lijinhong11.protectorapi.ProtectionCheckScheduler.schedule;
import static java.util.concurrent.CompletableFuture.completedFuture;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import java.util.concurrent.CompletableFuture;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GriefPreventionBlockProtectionModule implements IBlockProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "GriefPrevention";
    }

    @Override
    public boolean supportsAsync(@NotNull ProtectionCheck check) {
        // World configuration and permissions require the server thread. The async lookup
        // overrides capture the enabled datastore there, then query it on a worker thread.
        return false;
    }

    @Override
    public boolean isProtected(@NotNull World world, int chunkX, int chunkZ) {
        if (!GriefPrevention.instance.claimsEnabledForWorld(world)) {
            return false;
        }
        return hasClaim(GriefPrevention.instance.dataStore, world, chunkX, chunkZ);
    }

    @Override
    public CompletableFuture<Boolean> isProtectedAsync(@NotNull World world, int chunkX, int chunkZ) {
        return schedule(false, () -> enabledStore(world))
                .thenCompose(store -> store == null
                        ? completedFuture(false)
                        : schedule(true, () -> hasClaim(store, world, chunkX, chunkZ)));
    }

    @Override
    public CompletableFuture<Boolean> isProtectedAsync(@NotNull Location block) {
        Location snapshot = block.clone();
        return schedule(false, () -> enabledStore(snapshot.getWorld()))
                .thenCompose(store -> store == null
                        ? completedFuture(false)
                        : schedule(true, () -> store.getClaimAt(snapshot, true, null) != null));
    }

    private static DataStore enabledStore(World world) {
        GriefPrevention plugin = GriefPrevention.instance;
        return plugin.claimsEnabledForWorld(world) ? plugin.dataStore : null;
    }

    private static boolean hasClaim(DataStore store, World world, int chunkX, int chunkZ) {
        // getClaims returns a view of a mutable ArrayList. Use the same monitor as
        // datastore claim mutations, and consume both the list and boundaries under it.
        synchronized (store) {
            for (Claim claim : store.getClaims(chunkX, chunkZ)) {
                if (world.equals(claim.getGreaterBoundaryCorner().getWorld())) {
                    return true;
                }
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
