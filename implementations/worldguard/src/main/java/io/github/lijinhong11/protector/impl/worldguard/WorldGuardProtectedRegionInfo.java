package io.github.lijinhong11.protector.impl.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.lijinhong11.protectorapi.convertions.FlagMap;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldGuardProtectedRegionInfo implements IProtectionRange {
    private final ProtectedRegion protectedRegion;

    public WorldGuardProtectedRegionInfo(ProtectedRegion protectedRegion) {
        this.protectedRegion = protectedRegion;
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        Map<String, FlagState<?>> flagMap = new FlagMap();
        for (Map.Entry<Flag<?>, Object> entry : protectedRegion.getFlags().entrySet()) {
            Flag<?> flag = entry.getKey();
            if (flag.getDefault() instanceof Boolean) {
                Boolean b = (Boolean) flag.getDefault();
                flagMap.put(flag.getName(), FlagStates.fromNullableBoolean(b));
            } else {
                flagMap.put(entry.getKey().getName(), FlagStates.of(entry.getValue()));
            }
        }

        return flagMap;
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        return getFlags().get(flag);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        if (flag.getForWorldGuard() == null) {
            return FlagStates.UNSUPPORTED;
        }

        return getFlagState(flag.getForWorldGuard());
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return protectedRegion.getOwners().getPlayers().stream()
                .map(Bukkit::getOfflinePlayer)
                .collect(Collectors.toList());
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return protectedRegion.getMembers().getPlayers().stream()
                .map(Bukkit::getOfflinePlayer)
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return getAdmins().get(0);
    }
}
