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
package io.github.lijinhong11.protector.block_impl.funnyguilds;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FunnyGuildsBlockProtectionModule implements IBlockProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "FunnyGuilds";
    }

    @Override
    public boolean isProtected(Location block) {
        return !FunnyGuilds.getInstance()
                .getRegionManager()
                .findRegionAtLocation(block)
                .isEmpty();
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return ProtectionSystem.isProtected(
                        player,
                        block,
                        fakeBreakEvent(block.getBlock(), player),
                        GuildProtectionPermission.BLOCK_BREAK,
                        false)
                .isEmpty();
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return ProtectionSystem.isProtected(
                        player,
                        block,
                        fakePlaceEvent(block.getBlock(), player),
                        GuildProtectionPermission.BLOCK_PLACE,
                        true)
                .isEmpty();
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return !isProtected(block);
    }

    private BlockBreakEvent fakeBreakEvent(Block block, Player player) {
        return new BlockBreakEvent(block, player);
    }

    private BlockPlaceEvent fakePlaceEvent(Block block, Player player) {
        return new BlockPlaceEvent(
                block, block.getState(), block, new ItemStack(Material.AIR), player, true, EquipmentSlot.HAND);
    }
}
