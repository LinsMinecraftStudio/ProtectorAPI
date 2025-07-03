package io.github.lijinhong11.protector.impl;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import io.github.lijinhong11.protector.api.convertions.FlagMap;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.FlagState;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class WorldGuardProtectedRegionInfo implements ProtectionRangeInfo {
    private final ProtectedRegion protectedRegion;

    public WorldGuardProtectedRegionInfo(ProtectedRegion protectedRegion) {
        this.protectedRegion = protectedRegion;
    }

    @Override
    public Map<String, IFlagState<?>> getFlags() {
        Map<String, IFlagState<?>> flagMap = new FlagMap();
        for (Map.Entry<Flag<?>, Object> entry : protectedRegion.getFlags().entrySet()) {
            Flag<?> flag = entry.getKey();
            if (flag.getDefault() instanceof Boolean b) {
                flagMap.put(flag.getName(), FlagState.fromNullableBoolean(b));
            } else {
                flagMap.put(entry.getKey().getName(), FlagState.of(FlagState.FlagType.OBJECT, entry.getValue()));
            }
        }

        return flagMap;
    }

    @Override
    public IFlagState<?> getFlagState(String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public IFlagState<?> getFlagState(String flag, OfflinePlayer player) {
        return getFlags().get(flag);
    }

    @Override
    public IFlagState<?> getFlagState(CommonFlags flag) {
        return getFlagState(flag, null);
    }

    @Override
    public IFlagState<?> getFlagState(CommonFlags flag, OfflinePlayer player) {
        if (flag.getForWorldGuard() == null) {
            return FlagState.UNSUPPORTED;
        }

        return getFlagState(flag.getForWorldGuard());
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return protectedRegion.getOwners().getPlayers().stream().map(Bukkit::getOfflinePlayer).toList();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return protectedRegion.getMembers().getPlayers().stream().map(Bukkit::getOfflinePlayer).toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return getAdmins().get(0);
    }
}
