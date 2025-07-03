package io.github.lijinhong11.protector.impl.bentobox;

import io.github.lijinhong11.protector.api.IProtectionModule;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BentoBoxProtectionModule implements IProtectionModule {
    @Override
    public String getPluginName() {
        return "BentoBox";
    }

    @Override
    public boolean isInProtectionRange(Player player) {
        return isInProtectionRange(player.getLocation());
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(Player player) {
        return getProtectionRangeInfo(player.getLocation());
    }

    @Override
    public List<? extends ProtectionRangeInfo> getProtectionRangeInfos(OfflinePlayer player) {
        return List.of();
    }

    @Override
    public boolean isInProtectionRange(Location location) {
        return false;
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(@Nullable Location location) {
        return null;
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return false;
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        return null;
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        return null;
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {

    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {

    }
}
