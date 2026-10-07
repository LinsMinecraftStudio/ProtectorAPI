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
package io.github.lijinhong11.protector.block_impl.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TownyBlockProtectionModule implements IBlockProtectionModule {
    private final TownyAPI api = TownyAPI.getInstance();

    @Override
    public @NotNull String getPluginName() {
        return "Towny";
    }

    @Override
    public boolean supportsAsync(@NotNull ProtectionCheck check) {
        // Audited against 0.100.0.0: WorldCoord lookup reads TownyUniverse's ConcurrentHashMap.
        // Permission checks below read live block types and PlayerCacheUtil state.
        return check == ProtectionCheck.LOOKUP;
    }

    @Override
    public boolean isProtected(Location block) {
        return api.getTownBlock(block) != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return PlayerCacheUtil.getCachePermission(
                player, block, block.getBlock().getType(), TownyPermission.ActionType.DESTROY);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return PlayerCacheUtil.getCachePermission(
                player, block, block.getBlock().getType(), TownyPermission.ActionType.BUILD);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return PlayerCacheUtil.getCachePermission(
                player, block, block.getBlock().getType(), TownyPermission.ActionType.ITEM_USE);
    }
}
