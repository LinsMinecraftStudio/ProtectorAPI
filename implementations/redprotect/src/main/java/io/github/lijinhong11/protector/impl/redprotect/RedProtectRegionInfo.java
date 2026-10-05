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
package io.github.lijinhong11.protector.impl.redprotect;

import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedProtectRegionInfo implements IProtectionRange {
    private final Region region;

    public RedProtectRegionInfo(Region region) {
        this.region = region;
    }

    @Override
    public @NotNull String getId() {
        return region.getID();
    }

    @Override
    public @NotNull String getDisplayName() {
        return region.getName();
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        return new WorldCollection(region.getCenterLoc().getWorld());
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

        return getFlags().get(flag) == null
                ? FlagStates.UNSUPPORTED
                : getFlags().get(flag);
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
