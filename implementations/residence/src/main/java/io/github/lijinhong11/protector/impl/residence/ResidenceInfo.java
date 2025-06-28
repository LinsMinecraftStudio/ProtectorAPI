package io.github.lijinhong11.protector.impl.residence;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import io.github.lijinhong11.protector.api.convertions.FlagMap;
import io.github.lijinhong11.protector.api.flag.FlagState;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

class ResidenceInfo implements ProtectionRangeInfo {
    private final ClaimedResidence residence;
    private final ResidencePermissions permissions;

    public ResidenceInfo(ClaimedResidence residence) {
        this.residence = residence;
        this.permissions = residence.getPermissions();
    }

    @Override
    public Map<String, FlagState> getFlags() {
        return new FlagMap(permissions.getFlags(), FlagState::fromNullableBoolean);
    }

    @Override
    public FlagState getFlagState(String flag) {
        return FlagState.fromNullableBoolean(permissions.getFlags().get(flag));
    }

    @Override
    public FlagState getFlagState(String flag, OfflinePlayer player) {
        return FlagState.fromNullableBoolean(permissions.getPlayerFlags(player.getName()).get(flag));
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return residence.getTrustedPlayers().stream().map(r -> Bukkit.getOfflinePlayer(r.getUniqueId())).toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(residence.getOwner());
    }
}
