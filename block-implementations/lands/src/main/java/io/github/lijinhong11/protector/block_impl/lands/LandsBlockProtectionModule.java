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
package io.github.lijinhong11.protector.block_impl.lands;

import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.flag.CustomFlag;
import io.github.lijinhong11.protectorapi.flag.FlagRegisterable;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// terrible condition check
public class LandsBlockProtectionModule implements IBlockProtectionModule, FlagRegisterable {
    private final LandsIntegration api;

    public LandsBlockProtectionModule() {
        api = LandsIntegration.of(ProtectorAPI.getPluginHost());
    }

    @Override
    public @NotNull String getPluginName() {
        return "Lands";
    }

    @Override
    public boolean supportsAsync(@NotNull ProtectionCheck check) {
        // LandsAPI 7.15.20 explicitly permits getLandByUnloadedChunk on async threads.
        // Role checks and Flags.getInteract(Block) do not share that guarantee.
        return check == ProtectionCheck.LOOKUP;
    }

    @Override
    public boolean isProtected(Location block) {
        // Query both loaded and unloaded claims without asking Bukkit to load a chunk.
        return api.getLandByUnloadedChunk(block.getWorld(), block.getBlockX() >> 4, block.getBlockZ() >> 4) != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        LandWorld lw = getLandWorld(block);
        if (lw == null) {
            return false;
        }

        LandPlayer lp = api.getLandPlayer(player.getUniqueId());
        return !lw.hasRoleFlag(lp, block, Flags.BLOCK_BREAK, null, false);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        LandWorld lw = getLandWorld(block);
        if (lw == null) {
            return false;
        }

        LandPlayer lp = api.getLandPlayer(player.getUniqueId());
        return !lw.hasRoleFlag(lp, block, Flags.BLOCK_PLACE, null, false);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        LandWorld lw = getLandWorld(block);
        if (lw == null) {
            return false;
        }

        LandPlayer lp = api.getLandPlayer(player.getUniqueId());
        RoleFlag interact = Flags.getInteract(block.getBlock());
        if (interact == null) {
            return allowPlace(player, block);
        }

        return !lw.hasRoleFlag(lp, block, interact, null, false);
    }

    private LandWorld getLandWorld(Location loc) {
        return api.getWorld(loc.getWorld());
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        api.getFlagRegistry().register(new ProtectorAPIManagedFlag(flag));
    }
}
