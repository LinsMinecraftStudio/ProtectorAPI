package io.github.lijinhong11.protector.impl.dominion;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
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

public class DominionProtectionModule implements IProtectionModule {
    private final DominionAPI api;

    public DominionProtectionModule() {
        api = DominionAPI.getInstance();
    }

    @Override
    public String getPluginName() {
        return "Dominion";
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
        List<DominionDTO> list = api.getPlayerAdminDominionDTOs(player.getUniqueId());
        return list.stream().map(DominionRangeInfo::new).toList();
    }

    @Override
    public boolean isInProtectionRange(Location location) {
        return api.getDominion(location) != null;
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(@NotNull Location location) {
        DominionDTO dominion = api.getDominion(location);
        if (dominion == null) {
            return null;
        }

        return new DominionRangeInfo(dominion);
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return false;
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
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
