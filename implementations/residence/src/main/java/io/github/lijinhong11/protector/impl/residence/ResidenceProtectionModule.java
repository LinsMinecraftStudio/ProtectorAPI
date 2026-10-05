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
package io.github.lijinhong11.protector.impl.residence;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.flag.*;
import io.github.lijinhong11.protectorapi.handlers.RangeCreateHandler;
import io.github.lijinhong11.protectorapi.handlers.RangeDeleteHandler;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResidenceProtectionModule implements IProtectionModule, FlagRegisterable, Listener {
    public ResidenceProtectionModule() {
        Bukkit.getPluginManager().registerEvents(this, ProtectorAPI.getPluginHost());
    }

    @Override
    public @NotNull String getPluginName() {
        return "Residence";
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        List<String> list = ResidenceApi.getPlayerManager().getResidenceList(player.getName(), true);
        return list.stream()
                .map(ResidenceApi.getResidenceManager()::getByName)
                .map(ResidenceInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return ResidenceApi.getResidenceManager().getByLoc(location) != null;
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
        ClaimedResidence res = ResidenceApi.getResidenceManager().getByLoc(location);
        if (res == null) {
            return null;
        }

        return new ResidenceInfo(res);
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        FlagPermissions.addFlag(flag.id());
        Residence.getInstance()
                .getPermissionManager()
                .getAllFlags()
                .setFlag(
                        flag.id(),
                        flag.defaultValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return true;
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        Map<String, Boolean> flags =
                Residence.getInstance().getWorldFlags().getPerms(world).getFlags();
        return FlagStates.fromNullableBoolean(flags.get(flag));
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        return getGlobalFlag(flag.getForResidence(), world);
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {
        if (value instanceof Boolean b) {
            Residence.getInstance().getWorldFlags().getPerms(world).getFlags().put(flag, b);
        } else {
            throw new IllegalArgumentException("value must be a boolean in Residence");
        }
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {
        setGlobalFlag(world, flag.getForResidence(), value);
    }

    @EventHandler
    public void onCreated(ResidenceCreationEvent e) {
        ResidenceInfo info = new ResidenceInfo(e.getResidence());

        ProtectorAPI.getHandlers(RangeCreateHandler.class).forEach(a -> a.onCreate(this, info));
    }

    @EventHandler
    public void onDelete(ResidenceDeleteEvent e) {
        ResidenceInfo info = new ResidenceInfo(e.getResidence());

        ProtectorAPI.getHandlers(RangeDeleteHandler.class).forEach(a -> a.onDelete(this, info));
    }
}
