package io.github.lijinhong11.protector.block_impl.griefprevention;

import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GriefPreventionBlockProtectionModule implements IBlockProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "GriefPrevention";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return getClaim(block) != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        Claim c = getClaim(block);
        return c == null || c.hasExplicitPermission(player, ClaimPermission.Build);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return allowBreak(player, block);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        Claim c = getClaim(block);
        return c == null || c.hasExplicitPermission(player, ClaimPermission.Inventory);
    }

    private Claim getClaim(Location loc) {
        if (!GriefPrevention.instance.claimsEnabledForWorld(loc.getWorld())) {
            return null;
        }

        return GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
    }
}
