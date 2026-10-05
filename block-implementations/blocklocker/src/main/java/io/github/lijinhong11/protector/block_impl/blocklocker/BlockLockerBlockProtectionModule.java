package io.github.lijinhong11.protector.block_impl.blocklocker;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import nl.rutgerkok.blocklocker.BlockLockerAPIv2;
import nl.rutgerkok.blocklocker.BlockLockerPlugin;
import nl.rutgerkok.blocklocker.profile.Profile;
import nl.rutgerkok.blocklocker.protection.Protection;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockLockerBlockProtectionModule implements IBlockProtectionModule {
    private final BlockLockerPlugin api = BlockLockerAPIv2.getPlugin();

    @Override
    public @NotNull String getPluginName() {
        return "BlockLocker";
    }

    @Override
    public boolean isProtected(Location block) {
        return find(block).isPresent();
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return find(block)
                .map(p -> p.isAllowed(getProfile(player)))
                .orElse(true);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return find(block)
                .map(p -> p.isAllowed(getProfile(player)))
                .orElse(true);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return find(block)
                .map(p -> p.isAllowed(getProfile(player)))
                .orElse(true);
    }

    private Optional<Protection> find(Location loc) {
        Block block = loc.getBlock();
        return api.getProtectionFinder().findProtection(block);
    }

    private Profile getProfile(Player player) {
        return api.getProfileFactory().fromPlayer(player);
    }
}
