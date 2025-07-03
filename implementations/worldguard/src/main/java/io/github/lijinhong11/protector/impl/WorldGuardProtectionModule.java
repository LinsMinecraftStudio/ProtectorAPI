package io.github.lijinhong11.protector.impl;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.lijinhong11.protector.api.IProtectionModule;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.FlagState;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WorldGuardProtectionModule implements IProtectionModule {
    private final RegionContainer api;
    private final ConfigurationManager globalFlags;

    public WorldGuardProtectionModule() {
        api = WorldGuard.getInstance().getPlatform().getRegionContainer();
        globalFlags = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
    }

    @Override
    public String getPluginName() {
        return "WorldGuard";
    }

    @Override
    public boolean isInProtectionRange(Player player) {
        return isInProtectionRange(player.getLocation());
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(Player player) {
        return getProtectionRangeInfo(player.getLocation());
    }

    @Override
    public List<? extends ProtectionRangeInfo> getProtectionRangeInfos(OfflinePlayer player) {
        return new ArrayList<>(); // it is not supported
    }

    @Override
    public boolean isInProtectionRange(Location location) {
        return !api.createQuery().getApplicableRegions(toWELocation(location)).getRegions().isEmpty();
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(@Nullable Location location) {
        ProtectedRegion[] regions = api.createQuery().getApplicableRegions(toWELocation(location)).getRegions().toArray(ProtectedRegion[]::new);
        if (regions.length > 0) {
            return new WorldGuardProtectedRegionInfo(regions[0]);
        } else {
            return null;
        }
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return true;
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            return FlagState.UNSUPPORTED;
        }

        BukkitWorldConfiguration bwc = (BukkitWorldConfiguration) globalFlags.get(BukkitAdapter.adapt(w));
        Object o = bwc.getProperty(flag);
        if (o instanceof Boolean b) {
            return FlagState.fromNullableBoolean(b);
        } else {
            return FlagState.of(FlagState.FlagType.OBJECT, o);
        }
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        if (flag.getForWorldGuard() == null) {
            return FlagState.UNSUPPORTED;
        }

        return getGlobalFlag(flag.getForWorldGuard(), world);
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            return;
        }

        Location loc = new Location(w, 0, 0, 0);
        Set<ProtectedRegion> regions = api.createQuery().getApplicableRegions(toWELocation(loc)).getRegions();
        regions.stream().filter(region -> Objects.equals(region.getId(), "__global__")).findFirst().ifPresent(region -> {
            Flag<?> flag1 = WorldGuard.getInstance().getFlagRegistry().get(flag);
            if (flag1 == null) {
                return;
            }

            region.getFlags().put(flag1, flag1.unmarshal(value));
        });
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {
        if (flag.getForWorldGuard() == null) {
            return;
        }

        setGlobalFlag(world, flag.getForWorldGuard(), value);
    }

    private com.sk89q.worldedit.util.Location toWELocation(Location location) {
        return BukkitAdapter.adapt(location);
    }
}
