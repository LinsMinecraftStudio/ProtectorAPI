package io.github.lijinhong11.protector.impl.dominion;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import io.github.lijinhong11.protector.api.IProtectionModule;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class DominionProtectionModule implements IProtectionModule {
    private final DominionAPI api;

    public DominionProtectionModule() {
        api = DominionAPI.getInstance();
    }

    @Override
    public String getPluginName() {
        return "Dominion";
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
        return api.getDominion(location) != null;
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(Location location) {
        DominionDTO dominion = api.getDominion(location);
        if (dominion == null) {
            return null;
        }

        return new DominionRangeInfo(dominion);
    }
}
