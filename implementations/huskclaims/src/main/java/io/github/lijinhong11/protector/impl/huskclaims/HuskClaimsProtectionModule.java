package io.github.lijinhong11.protector.impl.huskclaims;

import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.FlagState;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import io.github.lijinhong11.protector.api.protection.IProtectionModule;
import io.github.lijinhong11.protector.api.protection.ProtectionRangeInfo;
import java.util.*;
import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.claim.ClaimWorld;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.position.World;
import net.william278.huskclaims.user.OnlineUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HuskClaimsProtectionModule implements IProtectionModule {
    private static final BukkitHuskClaimsAPI api = BukkitHuskClaimsAPI.getInstance();

    @Override
    public @NotNull String getPluginName() {
        return "HuskClaims";
    }

    @Override
    public List<? extends ProtectionRangeInfo> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        Map<ClaimWorld, List<Claim>> claims = api.getUserClaims(OnlineUser.of(player.getUniqueId(), player.getName()));
        List<HuskClaimsClaimInfo> list = new ArrayList<>();
        for (List<Claim> claims1 : claims.values()) {
            for (Claim claim : claims1) {
                list.add(new HuskClaimsClaimInfo(claim));
            }
        }

        return list;
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return api.isWorldClaimable(wrapWorld(location.getWorld())) && api.isClaimAt(wrapLocation(location));
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(@NotNull Location location) {
        if (!api.isWorldClaimable(wrapWorld(location.getWorld()))) {
            return null;
        }

        Optional<Claim> claim = api.getClaimAt(wrapLocation(location));
        return claim.map(HuskClaimsClaimInfo::new).orElse(null);
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return true;
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        Optional<OperationType> operationType = OperationType.get(flag);
        if (operationType.isEmpty()) {
            return FlagState.UNSUPPORTED;
        }

        Optional<ClaimWorld> cw = api.getClaimWorld(wrapWorld(Bukkit.getWorld(world)));
        if (cw.isEmpty()) {
            return FlagState.WORLD_NOT_FOUND;
        }

        return FlagState.fromBoolean(cw.get().getWildernessFlags().contains(operationType.get()));
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        if (flag.getForHuskClaims() == null) {
            return FlagState.UNSUPPORTED;
        }

        return getGlobalFlag(flag.getForHuskClaims(), world);
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {
        Optional<OperationType> type = OperationType.get(flag);
        type.ifPresent(t -> {
            Optional<ClaimWorld> cw = api.getClaimWorld(wrapWorld(Bukkit.getWorld(world)));
            cw.ifPresent(c -> {
                List<OperationType> wildernessFlags =
                        api.getPlugin().getSettings().getClaims().getWildernessRules();
                if (wildernessFlags.contains(t)) {
                    boolean b = value.equals(true);
                    if (!b) {
                        wildernessFlags.remove(t);
                    }
                } else {
                    if (value.equals(true)) {
                        wildernessFlags.add(t);
                    }
                }
            });
        });
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {
        if (flag.getForHuskClaims() == null) {
            return;
        }

        setGlobalFlag(world, flag.getForHuskClaims(), value);
    }

    static World wrapWorld(org.bukkit.World world) {
        return api.getWorld(world);
    }

    static Position wrapLocation(Location location) {
        return api.getPosition(location);
    }
}
