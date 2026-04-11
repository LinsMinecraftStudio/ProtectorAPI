package io.github.lijinhong11.protector.impl.residence;

import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import io.github.lijinhong11.protectorapi.objects.FlagMap;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResidenceInfo implements IProtectionRange {
    private final ClaimedResidence residence;
    private final ResidencePermissions permissions;

    public ResidenceInfo(ClaimedResidence residence) {
        this.residence = residence;
        this.permissions = residence.getPermissions();
    }

    @Override
    public @NotNull String getId() {
        return residence.getName();
    }

    @Override
    public @NotNull String getDisplayName() {
        return residence.getResidenceName();
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        World w = residence.getWorldName() != null ? Bukkit.getWorld(residence.getWorldName()) : null;

        return new WorldCollection(w);
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
                .collect(Collectors.toList());
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return residence.getTrustedPlayers().stream()
                .map(r -> Bukkit.getOfflinePlayer(r.getUniqueId()))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(residence.getOwner());
    }
}
