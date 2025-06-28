package io.github.lijinhong11.protector.impl.redprotect;

import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import io.github.lijinhong11.protector.api.convertions.FlagMap;
import io.github.lijinhong11.protector.api.flag.FlagState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class RedProtectRegionInfo implements ProtectionRangeInfo {
    private final Region region;

    public RedProtectRegionInfo(Region region) {
        this.region = region;
    }

    @Override
    public Map<String, FlagState> getFlags() {
        return new FlagMap(region.getFlags(), o -> {
           if (o instanceof Boolean b) {
               return FlagState.fromNullableBoolean(b);
           } else {
               return FlagState.NULL;
           }
        });
    }

    @Override
    public FlagState getFlagState(String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState getFlagState(String flag, OfflinePlayer player) {
        return FlagState.fromBoolean(region.getFlagBool(flag));
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return region.getAdmins().stream().map(r -> Bukkit.getOfflinePlayer(r.getUUID())).toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return region.getLeaders().stream().map(r -> Bukkit.getOfflinePlayer(r.getUUID())).findFirst().orElse(null);
    }
}
