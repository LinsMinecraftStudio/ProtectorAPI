package io.github.lijinhong11.protector.impl.redprotect;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import io.github.lijinhong11.protector.api.IProtectionModule;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class RedProtectProtectionModule implements IProtectionModule {
    private final RedProtectAPI api;

    public RedProtectProtectionModule() {
        api = RedProtect.get().getAPI();
    }

    @Override
    public String getPluginName() {
        return "RedProtect";
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
        return api.getRegion(location) != null;
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(Location location) {
        Region region = api.getRegion(location);
        if (region == null) {
            return null;
        }

        return new RedProtectRegionInfo(region);
    }
}
