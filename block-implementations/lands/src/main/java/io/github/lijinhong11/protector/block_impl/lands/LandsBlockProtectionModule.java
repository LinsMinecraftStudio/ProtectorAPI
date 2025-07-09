package io.github.lijinhong11.protector.block_impl.lands;

import io.github.lijinhong11.protector.api.ProtectorAPI;
import io.github.lijinhong11.protector.api.annotations.WeakImplementation;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// terrible condition check
@WeakImplementation
public class LandsBlockProtectionModule implements IBlockProtectionModule {
    private final LandsIntegration api;

    public LandsBlockProtectionModule() {
        api = LandsIntegration.of(ProtectorAPI.getPluginHost());
    }

    @Override
    public @NotNull String getPluginName() {
        return "Lands";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        LandWorld lw = getLandWorld(block);
        if (lw == null) {
            return false;
        }

        LandPlayer lp = api.getLandPlayer(player.getUniqueId());
        return !lw.hasRoleFlag(lp, block, Flags.BLOCK_BREAK, null, false);
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        LandWorld lw = getLandWorld(block);
        if (lw == null) {
            return false;
        }

        LandPlayer lp = api.getLandPlayer(player.getUniqueId());
        return !lw.hasRoleFlag(lp, block, Flags.BLOCK_BREAK, null, false);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        LandWorld lw = getLandWorld(block);
        if (lw == null) {
            return false;
        }

        LandPlayer lp = api.getLandPlayer(player.getUniqueId());
        return !lw.hasRoleFlag(lp, block, Flags.BLOCK_PLACE, null, false);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        LandWorld lw = getLandWorld(block);
        if (lw == null) {
            return false;
        }

        LandPlayer lp = api.getLandPlayer(player.getUniqueId());
        RoleFlag interact = Flags.getInteract(block.getBlock());
        if (interact == null) {
            return allowPlace(player, block);
        }

        return !lw.hasRoleFlag(lp, block, interact, null, false);
    }

    private LandWorld getLandWorld(Location loc) {
        return api.getWorld(loc.getWorld());
    }
}
