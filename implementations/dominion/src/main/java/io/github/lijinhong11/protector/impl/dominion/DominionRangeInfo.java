package io.github.lijinhong11.protector.impl.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import io.github.lijinhong11.protector.api.convertions.FlagMap;
import io.github.lijinhong11.protector.api.flag.FlagState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DominionRangeInfo implements ProtectionRangeInfo {
    private final DominionDTO dominion;

    public DominionRangeInfo(DominionDTO dominion) {
        this.dominion = dominion;
    }

    @Override
    public Map<String, FlagState> getFlags() {
        Map<String, FlagState> flagMap = new FlagMap();
        dominion.getEnvironmentFlagValue().forEach((f, b) -> flagMap.put(f.getFlagName(), FlagState.fromBoolean(b)));
        dominion.getGuestPrivilegeFlagValue().forEach((f, b) -> flagMap.put(f.getFlagName(), FlagState.fromBoolean(b)));
        return flagMap;
    }

    @Override
    public FlagState getFlagState(String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState getFlagState(String flag, OfflinePlayer player) {
        Flag dominionFlag = Flags.getFlag(flag);
        if (dominionFlag == null) {
            return FlagState.NULL;
        }

        if (dominionFlag instanceof EnvFlag ef) {
            return FlagState.fromBoolean(dominion.getEnvFlagValue(ef));
        }

        if (dominionFlag instanceof PriFlag pf) {
            return FlagState.fromBoolean(dominion.getGuestFlagValue(pf));
        }

        return FlagState.NULL;
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
    public @Nullable OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(dominion.getOwner());
    }
}
