package io.github.lijinhong11.protector.block_impl.shopchest;

import de.epiceric.shopchest.ShopChest;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopChestBlockProtectionModule implements IBlockProtectionModule {
    private final ShopChest api;

    public ShopChestBlockProtectionModule() {
        api = ShopChest.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "ShopChest";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return !api.getShopUtils().isShop(block);
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return !api.getShopUtils().isShop(block);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return true;
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return true;
    }
}
