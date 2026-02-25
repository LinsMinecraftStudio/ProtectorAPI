package io.github.lijinhong11.protector.impl.iridiumskyblock;

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.User;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRangeInfo;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IridiumSkyblockProtectionModule implements IProtectionModule {
    private final IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();

    @Override
    public @NotNull String getPluginName() {
        return "IridiumSkyblock";
    }

    @Override
    public List<? extends IProtectionRangeInfo> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        User user = api.getUser(player);
        return user.getIsland()
                .map(i -> List.of(new IridiumSkyblockIslandInfo(i)))
                .orElse(List.of());
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return api.getIslandViaLocation(location).isPresent();
    }

    @Override
    public @Nullable IProtectionRangeInfo getProtectionRangeInfo(@NotNull Location location) {
        return api.getIslandViaLocation(location)
                .map(IridiumSkyblockIslandInfo::new)
                .orElse(null);
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
