package io.github.lijinhong11.protector.block_impl.landclaimplugin;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;

import org.ayosynk.landClaimPlugin.api.LandClaimAPI;
import org.ayosynk.landClaimPlugin.models.ClaimProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LandClaimPluginBlockProtectionModule implements IBlockProtectionModule {
    private final LandClaimAPI api = LandClaimAPI.getInstance();

    @Override
    public @NotNull String getPluginName() {
        return "HuskTowns";
    }

    @Override
    public boolean isProtected(Location block) {
        return api.isLocationClaimed(block);
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        ClaimProfile profile = api.getClaimAt(block);
        if (profile == null) {
            return true;
        }
        return api.hasPermission(profile, player.getUniqueId(), "BLOCK_BREAK");
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        ClaimProfile profile = api.getClaimAt(block);
        if (profile == null) {
            return true;
        }
        return api.hasPermission(profile, player.getUniqueId(), "BLOCK_PLACE");
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        ClaimProfile profile = api.getClaimAt(block);
        if (profile == null) {
            return true;
        }
        return api.hasPermission(profile, player.getUniqueId(), "USE_CONTAINERS");
    }
}
