package io.github.lijinhong11.protector.impl.residence;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import io.github.lijinhong11.protector.api.IProtectionModule;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ResidenceProtectionModule implements IProtectionModule {
    @Override
    public String getPluginName() {
        return "Residence";
    }

    @Override
    public boolean isInProtectionRange(Player player) {
        return isInProtectionRange(player.getLocation());
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(Player player) {
        return getProtectionRangeInfo(player.getLocation());
    }

    @Override
    public boolean isInProtectionRange(Location location) {
        return ResidenceApi.getResidenceManager().getByLoc(location) != null;
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(Location location) {
        ClaimedResidence res = ResidenceApi.getResidenceManager().getByLoc(location);
        if (res == null) {
            return null;
        }

        return new ResidenceInfo(res);
    }
}
