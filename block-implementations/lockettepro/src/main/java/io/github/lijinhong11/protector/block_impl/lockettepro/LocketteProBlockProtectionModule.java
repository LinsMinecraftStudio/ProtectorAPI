package io.github.lijinhong11.protector.block_impl.lockettepro;

import io.github.lijinhong11.protectorapi.block.IBlockProtectionModule;
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
    public boolean isProtected(Player player, Location block) {
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
