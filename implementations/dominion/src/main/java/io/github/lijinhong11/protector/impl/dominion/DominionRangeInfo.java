package io.github.lijinhong11.protector.impl.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import io.github.lijinhong11.protectorapi.convertions.FlagMap;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IProtectionRangeInfo;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DominionRangeInfo implements IProtectionRangeInfo {
    private final DominionDTO dominion;

    public DominionRangeInfo(DominionDTO dominion) {
        this.dominion = dominion;
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
    public FlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        Flag dominionFlag = Flags.getFlag(flag);
        if (dominionFlag == null) {
            return FlagStates.UNSUPPORTED;
        }

        if (dominionFlag instanceof EnvFlag ef) {
            return FlagStates.fromNullableBoolean(dominion.getEnvFlagValue(ef));
        }

        if (dominionFlag instanceof PriFlag pf) {
            if (player == null) {
                return FlagStates.fromNullableBoolean(dominion.getGuestFlagValue(pf));
            } else {
                if (player.getUniqueId() == dominion.getOwner()) {
                    return FlagStates.fromNullableBoolean(dominion.getGuestFlagValue(pf));
                }

                Optional<MemberDTO> memberDTO = dominion.getMembers().stream()
                        .filter(m -> m.getPlayerUUID().equals(player.getUniqueId()))
                        .findFirst();

                if (memberDTO.isEmpty()) {
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
