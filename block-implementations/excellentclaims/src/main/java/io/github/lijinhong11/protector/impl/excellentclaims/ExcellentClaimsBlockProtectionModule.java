package io.github.lijinhong11.protector.impl.excellentclaims;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentclaims.api.ClaimsAPI;
import su.nightexpress.excellentclaims.api.claim.Claim;
import su.nightexpress.excellentclaims.api.claim.ClaimPermission;

public class ExcellentClaimsBlockProtectionModule implements IBlockProtectionModule {
    private final ClaimsAPI api;
    
    public ExcellentClaimsBlockProtectionModule() {
        api = Bukkit.getServicesManager().getRegistration(ClaimsAPI.class).getProvider();
    }
    
    @Override
    public @NotNull String getPluginName() {
        return "ExcellentClaims";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return api.getClaimRegistry().isClaimed(block);
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        for (Claim claim : api.getClaimRegistry().getAt(block)) {
            if (!api.getClaimPermissions().hasPermission(player, claim, ClaimPermission.BUILDING)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        for (Claim claim : api.getClaimRegistry().getAt(block)) {
            if (!api.getClaimPermissions().hasPermission(player, claim, ClaimPermission.BUILDING)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        for (Claim claim : api.getClaimRegistry().getAt(block)) {
            if (!api.getClaimPermissions().hasPermission(player, claim, ClaimPermission.BLOCK_INTERACT)) {
                return false;
            }
        }

        return true;
    }
}
