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
package io.github.lijinhong11.protector.impl.griefdefender;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.permission.Context;
import com.griefdefender.api.permission.flag.Flag;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.FlagMap;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GriefDefenderClaimInfo implements IProtectionRange {
    private final Claim claim;

    public GriefDefenderClaimInfo(Claim claim) {
        this.claim = claim;
    }

    @Override
    public @NotNull String getId() {
        return claim.getUniqueId().toString();
    }

    @Override
    public @NotNull String getDisplayName() {
        return claim.getDisplayName() == null ? "" : claim.getDisplayName();
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        return new WorldCollection(Bukkit.getWorld(claim.getWorldName()));
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        Set<Context> contexts = new HashSet<>();

        contexts.add(claim.getContext());

        FlagMap flagMap = new FlagMap();

        for (Flag flag : GriefDefender.getRegistry().getAllOf(Flag.class)) {
            flagMap.put(
                    flag.getId(),
                    triStateToFlagState(claim.getActiveFlagPermissionValue(
                            flag, GriefDefender.getCore().getDefaultSubject(), contexts)));
        }

        return flagMap;
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        return getFlags().get(flag);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        return getFlagState(flag);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        if (flag.getForGriefDefender() == null) {
            return null;
        }

        return getFlagState(flag.getForGriefDefender());
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        return getFlagState(flag);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return new ArrayList<>();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return claim.getUserTrusts().stream().map(Bukkit::getOfflinePlayer).toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(claim.getOwnerUniqueId());
    }

    private FlagState<Boolean> triStateToFlagState(Tristate tristate) {
        return switch (tristate) {
            case TRUE -> FlagStates.ALLOW;
            case FALSE -> FlagStates.DENY;
            case UNDEFINED -> FlagStates.NOT_SET;
        };
    }
}
