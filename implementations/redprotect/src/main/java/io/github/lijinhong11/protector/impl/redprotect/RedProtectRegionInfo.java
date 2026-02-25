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
import java.util.Objects;
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
            if (o instanceof Boolean b) {
                return FlagStates.fromNullableBoolean(b);
            } else {
                return FlagStates.of(o);
            }
        }));
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        Preconditions.checkNotNull(flag, "flag cannot be null");

        return Objects.requireNonNullElse(getFlags().get(flag), FlagStates.UNSUPPORTED);
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
                .toList();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return region.getMembers().stream()
                .map(r -> Bukkit.getOfflinePlayer(r.getUUID()))
                .toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return region.getLeaders().stream()
                .map(r -> Bukkit.getOfflinePlayer(r.getUUID()))
                .findFirst()
                .orElse(null);
    }
}
