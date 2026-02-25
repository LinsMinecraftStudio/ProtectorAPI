package io.github.lijinhong11.protector.block_impl.chestprotection;

import io.github.lijinhong11.protectorapi.block.IBlockProtectionModule;
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
    public boolean isProtected(Player player, Location block) {
        return check(player, block);
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
