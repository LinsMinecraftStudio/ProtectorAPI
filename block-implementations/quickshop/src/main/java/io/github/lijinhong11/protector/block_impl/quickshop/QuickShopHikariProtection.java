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

import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;
import com.ghostchu.quickshop.api.shop.permission.BuiltInShopPermission;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IChunkProtectionModule;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class QuickShopHikariProtection implements IBlockProtectionModule, IChunkProtectionModule {
    private final QuickShopAPI api;

    public QuickShopHikariProtection() {
        api = QuickShopAPI.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "QuickShop-Hikari";
    }

    @Override
    public boolean supportsAsync(@NotNull ProtectionCheck check) {
        return check == ProtectionCheck.LOOKUP;
    }

    @Override
    public boolean isProtected(@NotNull World world, int chunkX, int chunkZ) {
        return !Objects.requireNonNullElse(api.getShopManager().getShops(world.getName(), chunkX, chunkZ), new HashMap<>()).isEmpty();
    }

    @Override
    public boolean isProtected(Location block) {
        return getShop(block) != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return getShop(block) == null
                || getShop(block).playerAuthorize(player.getUniqueId(), BuiltInShopPermission.DELETE);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return true;
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return getShop(block) == null
                || getShop(block).playerAuthorize(player.getUniqueId(), BuiltInShopPermission.PURCHASE);
    }

    private Shop getShop(Location block) {
        return api.getShopManager().getShop(block);
    }
}
