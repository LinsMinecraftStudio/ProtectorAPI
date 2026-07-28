package io.github.lijinhong11.protector.impl.askyblock;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ASkyBlockIslandInfo implements IProtectionRange {
    private static final ASkyBlockAPI api = ASkyBlockAPI.getInstance();
    private final Island island;

    public ASkyBlockIslandInfo(Island island) {
        this.island = island;
    }

    @Override
    public @NotNull String getId() {
        return "";
    }

    @Override
    public @NotNull String getDisplayName() {
        return "";
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        return new WorldCollection(api.getIslandWorld(), api.getNetherWorld(), null);
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        Map<String, FlagState<?>> flags = new HashMap<>();
        for (Island.SettingsFlag flag : Island.SettingsFlag.values()) {
            flags.put(flag.name().toLowerCase(), FlagStates.fromBoolean(island.getIgsFlag(flag)));
        }

        return flags;
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        try {
            Island.SettingsFlag theFlag = Island.SettingsFlag.valueOf(flag.toUpperCase());
            return FlagStates.fromBoolean(island.getIgsFlag(theFlag));
        } catch (IllegalArgumentException e) {
            return FlagStates.UNSUPPORTED;
        }
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        return getFlagState(flag);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        if (flag.getForASkyBlock() == null) {
            return FlagStates.UNSUPPORTED;
        }

        return getFlagState(flag.getForASkyBlock());
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        return getFlagState(flag);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return new ArrayList<>();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return island.getMembers().stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(island.getOwner());
    }
}
