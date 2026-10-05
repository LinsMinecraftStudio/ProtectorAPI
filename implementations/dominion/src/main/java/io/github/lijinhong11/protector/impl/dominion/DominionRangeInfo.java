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
package io.github.lijinhong11.protector.impl.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
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

public class DominionRangeInfo implements IProtectionRange {
    private final DominionDTO dominion;

    public DominionRangeInfo(DominionDTO dominion) {
        this.dominion = dominion;
    }

    @Override
    public @NotNull String getId() {
        return String.valueOf(dominion.getId());
    }

    @Override
    public @NotNull String getDisplayName() {
        return dominion.getName();
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        return new WorldCollection(dominion.getWorld());
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        FlagMap flagMap = new FlagMap();
        dominion.getEnvironmentFlagValue().forEach((f, b) -> flagMap.put(f.getFlagName(), FlagStates.fromBoolean(b)));
        dominion.getGuestPrivilegeFlagValue()
                .forEach((f, b) -> flagMap.put(f.getFlagName(), FlagStates.fromBoolean(b)));
        return Collections.unmodifiableMap(flagMap);
    }

    @Override
    public FlagState<?> getFlagState(@Nullable String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@Nullable String flag, OfflinePlayer player) {
        if (flag == null) {
            return FlagStates.UNSUPPORTED;
        }

        Flag dominionFlag = Flags.getFlag(flag);
        if (dominionFlag == null) {
            return FlagStates.UNSUPPORTED;
        }

        if (dominionFlag instanceof EnvFlag) {
            EnvFlag ef = (EnvFlag) dominionFlag;
            return FlagStates.fromNullableBoolean(dominion.getEnvFlagValue(ef));
        }

        if (dominionFlag instanceof PriFlag) {
            PriFlag pf = (PriFlag) dominionFlag;
            if (player == null) {
                return FlagStates.fromNullableBoolean(dominion.getGuestFlagValue(pf));
            } else {
                if (player.getUniqueId() == dominion.getOwner()) {
                    return FlagStates.fromNullableBoolean(dominion.getGuestFlagValue(pf));
                }

                Optional<MemberDTO> memberDTO = dominion.getMembers().stream()
                        .filter(m -> m.getPlayerUUID().equals(player.getUniqueId()))
                        .findFirst();

                if (!memberDTO.isPresent()) {
                    return FlagStates.fromNullableBoolean(dominion.getGuestFlagValue(pf));
                }

                return FlagStates.fromNullableBoolean(memberDTO.get().getFlagValue(pf));
            }
        }

        return FlagStates.UNSUPPORTED;
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag.getForDominion());
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        return getFlagState(flag.getForDominion(), player);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        List<OfflinePlayer> admins = new ArrayList<>();
        for (MemberDTO member : dominion.getMembers()) {
            if (member.getFlagValue(Flags.ADMIN)) {
                admins.add(Bukkit.getOfflinePlayer(member.getPlayerUUID()));
            }
        }
        return admins;
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        List<OfflinePlayer> members = new ArrayList<>();
        for (MemberDTO member : dominion.getMembers()) {
            if (!member.getFlagValue(Flags.ADMIN)) {
                members.add(Bukkit.getOfflinePlayer(member.getPlayerUUID()));
            }
        }
        return members;
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(dominion.getOwner());
    }
}
