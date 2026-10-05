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
package io.github.lijinhong11.protector.block_impl.chestshop;

import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChestShopBlockProtectionModule implements IBlockProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "ChestShop";
    }

    @Override
    public boolean isProtected(Location block) {
        Block block1 = block.getBlock();
        if (!uBlock.couldBeShopContainer(block1)) {
            return false;
        }

        Sign sign = uBlock.getConnectedSign(block1);

        return sign != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        Block block1 = block.getBlock();
        if (!uBlock.couldBeShopContainer(block1)) {
            return false;
        }

        Sign sign = uBlock.getConnectedSign(block1);
        if (sign != null) {
            return ChestShopSign.hasPermission(player, Permission.OTHER_NAME_DESTROY, sign);
        }

        return true;
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        Block block1 = block.getBlock();
        if (!uBlock.couldBeShopContainer(block1)) {
            return false;
        }

        Sign sign = uBlock.getConnectedSign(block1);
        if (sign != null) {
            return Security.canPlaceSign(player, sign);
        }

        return true;
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        Block block1 = block.getBlock();
        if (!uBlock.couldBeShopContainer(block1)) {
            return true;
        }

        Sign sign = uBlock.getConnectedSign(block1);
        if (sign != null) {
            return Security.canView(player, block1, false);
        }

        return false;
    }
}
