package io.github.lijinhong11.protector.block_impl.bolt;

import io.github.lijinhong11.protectorapi.block.IBlockProtectionModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.popcraft.bolt.BoltAPI;
import org.popcraft.bolt.util.Permission;

public class BoltBlockProtectionModule implements IBlockProtectionModule {
    private final BoltAPI api;

    public BoltBlockProtectionModule() {
        api = Bukkit.getServer().getServicesManager().load(BoltAPI.class);
    }

    @Override
    public @NotNull String getPluginName() {
        return "Bolt";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return api.isProtected(block.getBlock());
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return api.canAccess(block.getBlock(), player, Permission.DESTROY);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return api.canAccess(block.getBlock(), player, Permission.INTERACT);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return api.canAccess(block.getBlock(), player, Permission.INTERACT);
    }
}
