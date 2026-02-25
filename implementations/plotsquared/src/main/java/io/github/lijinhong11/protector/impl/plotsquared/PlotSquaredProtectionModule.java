package io.github.lijinhong11.protector.impl.plotsquared;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.CustomFlag;
import io.github.lijinhong11.protectorapi.flag.FlagRegisterable;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRangeInfo;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlotSquaredProtectionModule implements IProtectionModule, FlagRegisterable {
    private final PlotAPI plotAPI;

    public PlotSquaredProtectionModule() {
        plotAPI = new PlotAPI();
    }

    @Override
    public @NotNull String getPluginName() {
        return "PlotSquared";
    }

    @Override
    public List<? extends IProtectionRangeInfo> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        return plotAPI.getPlayerPlots(getPlotPlayer(player)).stream()
                .map(PlotSquaredPlotInfo::new)
                .toList();
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        com.plotsquared.core.location.Location loc = wrapLocation(location);
        return loc.isPlotArea() || loc.isUnownedPlotArea();
    }

    @Override
    public @Nullable IProtectionRangeInfo getProtectionRangeInfo(@NotNull Location location) {
        com.plotsquared.core.location.Location loc = wrapLocation(location);
        if (loc.getPlot() == null) {
            return null;
        }

        return new PlotSquaredPlotInfo(loc.getPlot());
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        GlobalFlagContainer.getInstance().addFlag(new PSCustomFlag(flag));
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

    private com.plotsquared.core.location.Location wrapLocation(Location location) {
        return com.plotsquared.core.location.Location.at(
                location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private PlotPlayer<?> getPlotPlayer(OfflinePlayer player) {
        return plotAPI.wrapPlayer(player.getUniqueId());
    }
}
