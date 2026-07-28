package io.github.lijinhong11.protector.impl.askyblock;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ASkyBlockProtectionModule implements IProtectionModule {
    private final ASkyBlockAPI api = ASkyBlockAPI.getInstance();

    @Override
    public @NotNull String getPluginName() {
        return "ASkyBlock";
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        Island island = api.getIslandOwnedBy(player.getUniqueId());
        if (island == null) {
            return new ArrayList<>();
        }

        return Collections.singletonList(new ASkyBlockIslandInfo(island));
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return api.islandAtLocation(location);
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
        Island island = api.getIslandAt(location);
        if (island != null) {
            return new ASkyBlockIslandInfo(island);
        }

        return null;
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
