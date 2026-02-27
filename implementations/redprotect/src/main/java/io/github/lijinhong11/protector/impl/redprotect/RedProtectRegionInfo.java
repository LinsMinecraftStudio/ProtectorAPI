package io.github.lijinhong11.protector.impl.redprotect;

import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import com.google.common.base.Preconditions;
import io.github.lijinhong11.protectorapi.convertions.FlagMap;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IProtectionRangeInfo;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedProtectRegionInfo implements IProtectionRangeInfo {
    private final Region region;

    public RedProtectRegionInfo(Region region) {
        this.region = region;
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        return Collections.unmodifiableMap(new FlagMap(region.getFlags(), o -> {
            if (o instanceof Boolean) {
                Boolean b = (Boolean) o;
                return FlagStates.fromNullableBoolean(b);
            } else {
                return FlagStates.of(o);
            }
        }));
    }

    @Override
    public FlagState<?> getFlagState(@Nullable String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@Nullable String flag, OfflinePlayer player) {
        if (flag == null) {
            return FlagStates.UNSUPPORTED;
        }

        return getFlags().get(flag) == null ? FlagStates.UNSUPPORTED : getFlags().get(flag);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        return getFlagState(flag.getForRedProtect(), player);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return region.getAdmins().stream()
                .map(r -> Bukkit.getOfflinePlayer(r.getUUID()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return region.getMembers().stream()
                .map(r -> Bukkit.getOfflinePlayer(r.getUUID()))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return region.getLeaders().stream()
                .map(r -> Bukkit.getOfflinePlayer(r.getUUID()))
                .findFirst()
                .orElse(null);
    }
}
