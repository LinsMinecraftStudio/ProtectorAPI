package io.github.lijinhong11.protector.block_impl.nobuildplus;

import io.github.lijinhong11.protectorapi.block.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import p1xel.nobuildplus.API.NBPAPI;
import p1xel.nobuildplus.Flags;
import p1xel.nobuildplus.NoBuildPlus;

public class NoBuildPlusBlockProtectionModule implements IBlockProtectionModule {
    private final NBPAPI api = NoBuildPlus.getInstance().getAPI();

    @Override
    public @NotNull String getPluginName() {
        return "NoBuildPlus";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return api.isWorldEnabled(block.getWorld().getName());
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return api.canExecute(block.getWorld().getName(), Flags.destroy);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return api.canExecute(block.getWorld().getName(), Flags.build);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return api.canExecute(block.getWorld().getName(), Flags.use);
    }
}
