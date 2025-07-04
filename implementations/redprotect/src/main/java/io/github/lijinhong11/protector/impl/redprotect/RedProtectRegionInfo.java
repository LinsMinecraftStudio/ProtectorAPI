package io.github.lijinhong11.protector.impl.redprotect;

import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import io.github.lijinhong11.protector.api.convertions.FlagMap;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.FlagState;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RedProtectRegionInfo implements ProtectionRangeInfo {
    private final Region region;

    public RedProtectRegionInfo(Region region) {
        this.region = region;
    }

    @Override
    public @NotNull Map<String, IFlagState<?>> getFlags() {
        return Collections.unmodifiableMap(new FlagMap(region.getFlags(), o -> {
           if (o instanceof Boolean b) {
               return FlagState.fromNullableBoolean(b);
           } else {
               return FlagState.of(FlagState.FlagType.OBJECT, o);
           }
        }));
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        return Objects.requireNonNullElse(getFlags().get(flag), FlagState.UNSUPPORTED);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag.getForRedProtect(), null);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        return getFlagState(flag.getForRedProtect(), player);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return region.getAdmins().stream().map(r -> Bukkit.getOfflinePlayer(r.getUUID())).toList();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return region.getMembers().stream().map(r -> Bukkit.getOfflinePlayer(r.getUUID())).toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return region.getLeaders().stream().map(r -> Bukkit.getOfflinePlayer(r.getUUID())).findFirst().orElse(null);
    }
}
