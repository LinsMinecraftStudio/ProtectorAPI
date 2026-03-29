package io.github.lijinhong11.protector.impl.iridiumskyblock;

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.dependencies.iridiumteams.PermissionType;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return island.getOwner().map(u -> Bukkit.getOfflinePlayer(u.getUuid())).orElse(null);
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
