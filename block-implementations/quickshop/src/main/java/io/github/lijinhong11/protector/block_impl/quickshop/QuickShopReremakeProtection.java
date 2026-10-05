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
package io.github.lijinhong11.protector.block_impl.quickshop;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.shop.Shop;

public class QuickShopReremakeProtection implements IBlockProtectionModule {
    private final QuickShop api;

    public QuickShopReremakeProtection() {
        api = QuickShop.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "QuickShop-Reremake";
    }

    @Override
    public boolean isProtected(Location block) {
        return getShop(block) != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return api.getPermissionChecker().canBuild(player, block).isSuccess();
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return api.getPermissionChecker().canBuild(player, block).isSuccess()
                && api.getShopManager()
                        .canBuildShop(
                                player, block.getBlock(), player.getFacing().getOppositeFace());
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return true;
    }

    private Shop getShop(Location block) {
        return api.getShopManager().getShop(block);
    }
}
