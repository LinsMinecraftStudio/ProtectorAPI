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
package io.github.lijinhong11.protector.impl.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.FlagMap;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldGuardProtectedRegionInfo implements IProtectionRange {
    private final World world;
    private final ProtectedRegion protectedRegion;

    public WorldGuardProtectedRegionInfo(World world, ProtectedRegion protectedRegion) {
        this.world = world;
        this.protectedRegion = protectedRegion;
    }

    @Override
    public @NotNull String getId() {
        return protectedRegion.getId();
    }

    @Override
    public @NotNull String getDisplayName() {
        return protectedRegion.getId();
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        return new WorldCollection(world);
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
