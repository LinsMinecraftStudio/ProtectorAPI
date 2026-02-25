package io.github.lijinhong11.protector.block_impl.quickshop;

import io.github.lijinhong11.protectorapi.block.IBlockProtectionModule;
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
    public boolean isProtected(Player player, Location block) {
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
