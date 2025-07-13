package io.github.lijinhong11.protector.block_impl.chestshop;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopInfoEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
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
    public boolean isProtected(Player player, Location block) {
        Block block1 = block.getBlock();
        if (!uBlock.couldBeShopContainer(block1)) {
            return false;
        }

        Sign sign = uBlock.getConnectedSign(block1);
        if (sign != null) {
            return Security.canAccess(player, block1);
        }

        return false;
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
