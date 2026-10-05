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
package io.github.lijinhong11.protector.impl.iridiumskyblock;

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.api.IslandCreateEvent;
import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
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

public class IridiumSkyblockProtectionModule implements IProtectionModule, Listener {
    private final IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();

    public IridiumSkyblockProtectionModule() {
        Bukkit.getPluginManager().registerEvents(this, ProtectorAPI.getPluginHost());
    }

    @Override
    public @NotNull String getPluginName() {
        return "IridiumSkyblock";
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        User user = api.getUser(player);
        return user.getIsland()
                .map(i -> Collections.singletonList(new IridiumSkyblockIslandInfo(i)))
                .orElse(new ArrayList<>());
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return api.getIslandViaLocation(location).isPresent();
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
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

    @EventHandler
    public void onCreated(IslandCreateEvent e) {
        Optional<Island> island = e.getUser().getIsland();
        if (!island.isPresent()) {
            return;
        }

        IridiumSkyblockIslandInfo info = new IridiumSkyblockIslandInfo(island.get());

        ProtectorAPI.getHandlers(RangeCreateHandler.class).forEach(a -> a.onCreate(this, info));
    }

    @EventHandler
    public void onDelete(IslandDeleteEvent e) {
        IridiumSkyblockIslandInfo info = new IridiumSkyblockIslandInfo(e.getIsland());

        ProtectorAPI.getHandlers(RangeDeleteHandler.class).forEach(a -> a.onDelete(this, info));
    }
}
