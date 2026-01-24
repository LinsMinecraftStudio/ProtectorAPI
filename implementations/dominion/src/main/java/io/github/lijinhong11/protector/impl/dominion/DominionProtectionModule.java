package io.github.lijinhong11.protector.impl.dominion;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.CustomFlag;
import io.github.lijinhong11.protector.api.flag.FlagRegisterable;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import io.github.lijinhong11.protector.api.protection.IProtectionModule;
import io.github.lijinhong11.protector.api.protection.IProtectionRangeInfo;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DominionProtectionModule implements IProtectionModule, FlagRegisterable {
    private final DominionAPI api;

    public DominionProtectionModule() {
        api = DominionAPI.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "Dominion";
    }

    @Override
    public List<? extends IProtectionRangeInfo> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        List<DominionDTO> list = api.getPlayerAdminDominionDTOs(player.getUniqueId());
        return list.stream().map(DominionRangeInfo::new).toList();
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return api.getDominion(location) != null;
    }

    @Override
    public @Nullable IProtectionRangeInfo getProtectionRangeInfo(@NotNull Location location) {
        DominionDTO dominion = api.getDominion(location);
        if (dominion == null) {
            return null;
        }

        return new DominionRangeInfo(dominion);
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        String displayName = flag.displayName() == null ? flag.id() : flag.displayName();
        String description = flag.description() == null ? "" : flag.description();
        PriFlag flag1 = new PriFlag(flag.id(), displayName, description, flag.defaultValue(), true);
        Flags.getAllPriFlags().add(flag1); // is that a great way?
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
