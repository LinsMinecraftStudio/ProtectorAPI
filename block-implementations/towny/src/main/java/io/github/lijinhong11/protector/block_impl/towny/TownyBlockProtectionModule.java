package io.github.lijinhong11.protector.block_impl.towny;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import io.github.lijinhong11.protectorapi.block.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TownyBlockProtectionModule implements IBlockProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "Towny";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return PlayerCacheUtil.getCachePermission(
                player, block, block.getBlock().getType(), TownyPermission.ActionType.BUILD);
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
