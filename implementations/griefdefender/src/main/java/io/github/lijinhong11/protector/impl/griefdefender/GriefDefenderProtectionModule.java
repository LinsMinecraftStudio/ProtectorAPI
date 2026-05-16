package io.github.lijinhong11.protector.impl.griefdefender;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GriefDefenderProtectionModule implements IProtectionModule {
    private final Core api = GriefDefender.getCore();

    @Override
    public @NotNull String getPluginName() {
        return "GriefDefender";
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        return api.getAllPlayerClaims(player.getUniqueId()).stream().map(GriefDefenderClaimInfo::new).collect(Collectors.toList());
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        Claim cl = api.getClaimAt(location);
        return cl != null && !cl.isWilderness();
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
        return api.getClaimAt(location) == null ? null : new GriefDefenderClaimInfo(api.getClaimAt(location));
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return false;
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {
        throw new UnsupportedOperationException();
    }
}
