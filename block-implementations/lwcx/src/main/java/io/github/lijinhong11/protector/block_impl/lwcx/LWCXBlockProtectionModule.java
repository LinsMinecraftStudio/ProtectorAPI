package io.github.lijinhong11.protector.block_impl.lwcx;

import com.griefcraft.lwc.LWC;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LWCXBlockProtectionModule implements IBlockProtectionModule {
    private final LWC lwc;

    public LWCXBlockProtectionModule() {
        this.lwc = LWC.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "LWC";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return lwc.isProtectable(block.getBlock());
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
        if (!lwc.isProtectable(loc.getBlock())) {
            return true;
        } else if (lwc.getProtectionCache().getProtection(loc.getBlock()) == null) {
            return true;
        }

        return lwc.canAccessProtection(p, loc.getBlock());
    }
}
