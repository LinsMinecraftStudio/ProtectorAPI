package io.github.lijinhong11.protector.impl.residence;

import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import io.github.lijinhong11.protectorapi.convertions.FlagMap;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IProtectionRangeInfo;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResidenceInfo implements IProtectionRangeInfo {
    private final ClaimedResidence residence;
    private final ResidencePermissions permissions;

    public ResidenceInfo(ClaimedResidence residence) {
        this.residence = residence;
        this.permissions = residence.getPermissions();
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        return Collections.unmodifiableMap(new FlagMap(permissions.getFlags(), FlagStates::fromNullableBoolean));
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        return FlagStates.fromNullableBoolean(permissions.getFlags().get(flag));
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        return FlagStates.fromNullableBoolean(
                permissions.getPlayerFlags(player.getName()).get(flag));
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag.getForResidence());
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        return getFlagState(flag.getForResidence(), player);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return getMembers().stream()
                .filter(r -> permissions.playerHas(r.getName(), Flags.admin, false))
                .toList();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return residence.getTrustedPlayers().stream()
                .map(r -> Bukkit.getOfflinePlayer(r.getUniqueId()))
                .toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(residence.getOwner());
    }
}
