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
package io.github.lijinhong11.protector.impl.iridiumskyblock;

import com.iridium.iridiumskyblock.PermissionType;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IridiumSkyblockIslandInfo implements IProtectionRange {
    private final IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();
    private final Island island;

    public IridiumSkyblockIslandInfo(Island island) {
        this.island = island;
    }

    @Override
    public @NotNull String getId() {
        return String.valueOf(island.getId());
    }

    @Override
    public @NotNull String getDisplayName() {
        return island.getName();
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        return new WorldCollection(island.getHome().getWorld());
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        return new HashMap<>();
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        return FlagStates.UNSUPPORTED;
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        PermissionType type = getPermissionType(flag);
        if (type == null) {
            return FlagStates.UNSUPPORTED;
        }

        return FlagStates.fromBoolean(api.getIslandPermission(island, api.getUser(player), type));
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return FlagStates.UNSUPPORTED;
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        PermissionType type = getPermissionType(flag.getForIridiumSkyblock());
        if (type == null) {
            return FlagStates.UNSUPPORTED;
        }

        return FlagStates.fromBoolean(api.getIslandPermission(island, api.getUser(player), type));
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return island.getMembers().stream()
                .filter(u -> api.getIslandPermission(island, u, PermissionType.CHANGE_PERMISSIONS))
                .map(u -> Bukkit.getOfflinePlayer(u.getUuid()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return island.getMembers().stream()
                .map(u -> Bukkit.getOfflinePlayer(u.getUuid()))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(island.getOwner().getUuid());
    }

    private PermissionType getPermissionType(String s) {
        if (s == null) {
            return null;
        }

        try {
            return PermissionType.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
