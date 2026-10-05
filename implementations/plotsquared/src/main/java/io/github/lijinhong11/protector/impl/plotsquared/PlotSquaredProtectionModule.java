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
package io.github.lijinhong11.protector.impl.plotsquared;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.events.PlayerAutoPlotEvent;
import com.plotsquared.core.events.PlayerAutoPlotsChosenEvent;
import com.plotsquared.core.events.PlayerClaimPlotEvent;
import com.plotsquared.core.events.PlotDeleteEvent;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.CustomFlag;
import io.github.lijinhong11.protectorapi.flag.FlagRegisterable;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.handlers.RangeCreateHandler;
import io.github.lijinhong11.protectorapi.handlers.RangeDeleteHandler;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlotSquaredProtectionModule implements IProtectionModule, FlagRegisterable {
    private final PlotAPI plotAPI;

    public PlotSquaredProtectionModule() {
        plotAPI = new PlotAPI();

        plotAPI.registerListener(this);
    }

    @Override
    public @NotNull String getPluginName() {
        return "PlotSquared";
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        return plotAPI.getPlayerPlots(getPlotPlayer(player)).stream()
                .map(PlotSquaredPlotInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        com.plotsquared.core.location.Location loc = wrapLocation(location);
        return loc.isPlotArea() || loc.isUnownedPlotArea();
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
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

    @Subscribe
    public void onCreated(PlayerAutoPlotEvent e) {
        PlotSquaredPlotInfo info = new PlotSquaredPlotInfo(e.getPlot());

        ProtectorAPI.getHandlers(RangeCreateHandler.class).forEach(a -> a.onCreate(this, info));
    }

    @Subscribe
    public void onCreated(PlayerAutoPlotsChosenEvent e) {
        PlotSquaredPlotInfo info = new PlotSquaredPlotInfo(e.getPlot());

        ProtectorAPI.getHandlers(RangeCreateHandler.class).forEach(a -> a.onCreate(this, info));
    }

    @Subscribe
    public void onCreated(PlayerClaimPlotEvent e) {
        PlotSquaredPlotInfo info = new PlotSquaredPlotInfo(e.getPlot());

        ProtectorAPI.getHandlers(RangeCreateHandler.class).forEach(a -> a.onCreate(this, info));
    }

    @Subscribe
    public void onDelete(PlotDeleteEvent e) {
        PlotSquaredPlotInfo info = new PlotSquaredPlotInfo(e.getPlot());

        ProtectorAPI.getHandlers(RangeDeleteHandler.class).forEach(a -> a.onDelete(this, info));
    }
}
