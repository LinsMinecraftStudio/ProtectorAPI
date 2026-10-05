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
package io.github.lijinhong11.protector.impl.bentobox;

import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.handlers.RangeCreateHandler;
import io.github.lijinhong11.protectorapi.handlers.RangeDeleteHandler;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.island.IslandCreatedEvent;
import world.bentobox.bentobox.api.events.island.IslandDeletedEvent;
import world.bentobox.bentobox.database.objects.Island;

public class BentoBoxProtectionModule implements IProtectionModule, Listener {
    private final BentoBox bentoBox;

    public BentoBoxProtectionModule() {
        this.bentoBox = BentoBox.getInstance();

        Bukkit.getPluginManager().registerEvents(this, ProtectorAPI.getPluginHost());
    }

    @Override
    public @NotNull String getPluginName() {
        return "BentoBox";
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        Collection<Island> islands = bentoBox.getIslands().getIslands();
        if (islands.isEmpty()) {
            return Collections.emptyList();
        }

        for (Island island : islands) {
            if (island.getMembers().containsKey(player.getUniqueId())) {
                return Collections.singletonList(new BentoBoxIslandInfo(island));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return false;
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
        Optional<Island> island = bentoBox.getIslands().getIslandAt(location);
        return island.map(BentoBoxIslandInfo::new).orElse(null);
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

    @EventHandler
    public void onCreated(IslandCreatedEvent e) {
        BentoBoxIslandInfo info = new BentoBoxIslandInfo(e.getIsland());

        ProtectorAPI.getHandlers(RangeCreateHandler.class).forEach(a -> a.onCreate(this, info));
    }

    @EventHandler
    public void onDelete(IslandDeletedEvent e) {
        BentoBoxIslandInfo info = new BentoBoxIslandInfo(e.getIsland());

        ProtectorAPI.getHandlers(RangeDeleteHandler.class).forEach(a -> a.onDelete(this, info));
    }
}
