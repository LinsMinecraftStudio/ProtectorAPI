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
package io.github.lijinhong11.protector.block_impl.lockettepro;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import me.crafter.mc.lockettepro.LocketteProAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LocketteProBlockProtectionModule implements IBlockProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "LockettePro";
    }

    @Override
    public boolean isProtected(Location block) {
        return LocketteProAPI.isProtected(block.getBlock());
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return isUserOnSign(player, block);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return isUserOnSign(player, block);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return isUserOnSign(player, block);
    }

    private boolean isUserOnSign(Player p, Location block) {
        return LocketteProAPI.isUserOnSign(block.getBlock(), p);
    }
}
