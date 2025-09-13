package io.github.lijinhong11.protector.impl.plotsquared;

import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.PlotFlag;
import io.github.lijinhong11.protector.api.convertions.FlagMap;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.FlagState;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import io.github.lijinhong11.protector.api.protection.ProtectionRangeInfo;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlotSquaredPlotInfo implements ProtectionRangeInfo {
    private final Plot plot;

    public PlotSquaredPlotInfo(Plot plot) {
        this.plot = plot;
    }

    @Override
    public @NotNull Map<String, IFlagState<?>> getFlags() {
        FlagMap flagMap = new FlagMap();
        for (PlotFlag<?, ?> flag : plot.getFlags()) {
            flagMap.put(flag.getName(), FlagState.of(flag.getValue()));
        }
        return Collections.unmodifiableMap(flagMap);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        return getFlags().get(flag);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag, null);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        if (flag.getForPlotSquared() == null) {
            return FlagState.UNSUPPORTED;
        }

        return getFlagState(flag.getForPlotSquared(), null);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return getMembers();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return plot.getMembers().stream().map(Bukkit::getOfflinePlayer).toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        if (plot.getOwner() == null) {
            return null;
        }

        return Bukkit.getOfflinePlayer(plot.getOwner());
    }
}
