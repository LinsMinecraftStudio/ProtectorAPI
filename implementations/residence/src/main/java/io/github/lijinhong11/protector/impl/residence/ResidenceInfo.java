/*
 * ProtectorAPI
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.protector.impl.residence;

import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.FlagMap;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                permissions.getPlayerFlags(player.getUniqueId()).get(flag));
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
        return Bukkit.getOfflinePlayer(residence.getOwnerUUID());
    }
}
