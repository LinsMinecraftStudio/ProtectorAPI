package io.github.lijinhong11.protector.block_impl.funnyguilds;

import io.github.lijinhong11.protectorapi.block.IBlockProtectionModule;
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
    public boolean isProtected(Player player, Location block) {
        return !ProtectionSystem.isProtected(
                        player,
                        block,
                        fakeBreakEvent(block.getBlock(), player),
                        GuildProtectionPermission.BLOCK_BREAK,
                        false)
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
        return !isProtected(player, block);
    }

    private BlockBreakEvent fakeBreakEvent(Block block, Player player) {
        return new BlockBreakEvent(block, player);
    }

    private BlockPlaceEvent fakePlaceEvent(Block block, Player player) {
        return new BlockPlaceEvent(
                block, block.getState(), block, new ItemStack(Material.AIR), player, true, EquipmentSlot.HAND);
    }
}
