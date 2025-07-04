package io.github.lijinhong11.protector.impl.residence;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import io.github.lijinhong11.protector.api.IProtectionModule;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.FlagState;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
    public List<? extends ProtectionRangeInfo> getProtectionRangeInfos(OfflinePlayer player) {
        List<String> list = ResidenceApi.getPlayerManager().getResidenceList(player.getName(), true);
        return list.stream().map(ResidenceApi.getResidenceManager()::getByName).map(ResidenceInfo::new).toList();
    }

    @Override
    public boolean isInProtectionRange(Location location) {
        return ResidenceApi.getResidenceManager().getByLoc(location) != null;
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(@NotNull Location location) {
        ClaimedResidence res = ResidenceApi.getResidenceManager().getByLoc(location);
        if (res == null) {
            return null;
        }

        return new ResidenceInfo(res);
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return true;
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        Map<String, Boolean> flags = Residence.getInstance().wmanager.getPerms(world).getFlags();
        return FlagState.fromNullableBoolean(flags.get(flag));
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        return getGlobalFlag(flag.getForResidence(), world);
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {
        if (value instanceof Boolean b) {
            Residence.getInstance().wmanager.getPerms(world).getFlags().put(flag, b);
        } else {
            throw new IllegalArgumentException("value must be a boolean in Residence");
        }
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {
        setGlobalFlag(world, flag.getForResidence(), value);
    }
}
