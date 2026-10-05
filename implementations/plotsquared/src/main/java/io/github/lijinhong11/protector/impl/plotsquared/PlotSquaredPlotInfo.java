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

import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.PlotFlag;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.FlagMap;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlotSquaredPlotInfo implements IProtectionRange {
    private final Plot plot;

    public PlotSquaredPlotInfo(Plot plot) {
        this.plot = plot;
    }

    @Override
    public @NotNull String getId() {
        return plot.getId().toString();
    }

    @Override
    public @NotNull String getDisplayName() {
        return plot.getAlias();
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        World w = plot.getWorldName() != null ? Bukkit.getWorld(plot.getWorldName()) : null;

        return new WorldCollection(w);
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        FlagMap flagMap = new FlagMap();
        for (PlotFlag<?, ?> flag : plot.getFlags()) {
            flagMap.put(flag.getName(), FlagStates.of(flag.getValue()));
        }
        return Collections.unmodifiableMap(flagMap);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        return getFlags().get(flag);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        if (flag.getForPlotSquared() == null) {
            return FlagStates.UNSUPPORTED;
        }

        return getFlagState(flag.getForPlotSquared(), null);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return getMembers();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return plot.getMembers().stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        if (plot.getOwner() == null) {
            return null;
        }

        return Bukkit.getOfflinePlayer(plot.getOwner());
    }
}
