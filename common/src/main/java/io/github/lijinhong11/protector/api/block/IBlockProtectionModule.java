package io.github.lijinhong11.protector.api.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface IBlockProtectionModule {
    boolean isProtected(Player player, Location block);

    boolean allowBreak(Player player, Location block);

    boolean allowPlace(Player player, Location block);

    boolean allowInteract(Player player, Location block);

    default boolean isProtected(Player player, Block block) {
        return isProtected(player, block.getLocation());
    }

    default boolean allowBreak(Player player, Block block) {
        return allowBreak(player, block.getLocation());
    }

    default boolean allowPlace(Player player, Block block) {
        return allowPlace(player, block.getLocation());
    }

    default boolean allowInteract(Player player, Block block) {
        return allowInteract(player, block.getLocation());
    }
}
