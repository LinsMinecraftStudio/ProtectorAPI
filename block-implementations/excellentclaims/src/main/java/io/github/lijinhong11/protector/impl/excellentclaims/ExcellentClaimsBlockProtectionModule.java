package io.github.lijinhong11.protector.impl.excellentclaims;

import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentclaims.ClaimsAPI;

public class ExcellentClaimsBlockProtectionModule implements IBlockProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "ExcellentClaims";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return ClaimsAPI.getClaimManager().isClaimed(block);
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return ClaimsAPI.getClaimManager().canBreak(player, block);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return ClaimsAPI.getClaimManager().canBuild(player, block);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return ClaimsAPI.getClaimManager().canUseBlock(player, block.getBlock(), null);
    }
}
