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
package io.github.lijinhong11.protector.block_impl.chestprotection;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import me.angeschossen.chestprotect.api.ChestProtectAPI;
import me.angeschossen.chestprotect.api.protection.ProtectionManager;
import me.angeschossen.chestprotect.api.protection.block.BlockProtection;
import me.angeschossen.chestprotect.api.protection.world.ProtectionWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChestProtectionBlockProtectionModule implements IBlockProtectionModule {
    private final ChestProtectAPI api;

    public ChestProtectionBlockProtectionModule() {
        api = ChestProtectAPI.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "ChestProtection";
    }

    @Override
    public boolean isProtected(Location block) {
        ProtectionManager manager = api.getProtectionManager();
        if (!manager.isProtectableBlock(block.getBlock().getType())) {
            return true;
        }

        ProtectionWorld world = api.getProtectionWorld(block.getWorld());
        if (world == null) {
            return true;
        }

        BlockProtection protection = world.getBlockProtection(block.getBlockX(), block.getBlockY(), block.getBlockZ());
        return protection != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return check(player, block);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return check(player, block);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return check(player, block);
    }

    private boolean check(Player p, Location loc) {
        ProtectionManager manager = api.getProtectionManager();
        if (!manager.isProtectableBlock(loc.getBlock().getType())) {
            return true;
        }

        ProtectionWorld world = api.getProtectionWorld(loc.getWorld());
        if (world == null) {
            return true;
        }

        BlockProtection protection = world.getBlockProtection(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        return protection == null || protection.isTrusted(p.getUniqueId());
    }
}
