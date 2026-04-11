package io.github.lijinhong11.protector.block_impl.quickshop;

import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;
import com.ghostchu.quickshop.api.shop.permission.BuiltInShopPermission;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuickShopHikariProtection implements IBlockProtectionModule {
    private final QuickShopAPI api;

    public QuickShopHikariProtection() {
        api = QuickShopAPI.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "QuickShop-Hikari";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
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
