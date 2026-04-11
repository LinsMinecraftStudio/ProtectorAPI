package io.github.lijinhong11.protector.impl.huskclaims;

import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.flag.*;
import io.github.lijinhong11.protectorapi.handlers.RangeCreateHandler;
import io.github.lijinhong11.protectorapi.handlers.RangeDeleteHandler;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import net.kyori.adventure.key.Key;
import net.william278.huskclaims.BukkitHuskClaims;
import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.api.HuskClaimsAPI;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.claim.ClaimWorld;
import net.william278.huskclaims.claim.Region;
import net.william278.huskclaims.event.BukkitCreateClaimEvent;
import net.william278.huskclaims.event.BukkitDeleteClaimEvent;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import net.william278.huskclaims.libraries.cloplib.operation.OperationTypeRegistry;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.position.ServerWorld;
import net.william278.huskclaims.position.World;
import net.william278.huskclaims.user.OnlineUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class HuskClaimsProtectionModule implements IProtectionModule, FlagRegisterable, Listener {
    private static final BukkitHuskClaimsAPI api = BukkitHuskClaimsAPI.getInstance();

    public HuskClaimsProtectionModule() {
        Bukkit.getPluginManager().registerEvents(this, ProtectorAPI.getPluginHost());
    }

    static World wrapWorld(org.bukkit.World world) {
        return api.getWorld(world);
    }

    static Position wrapLocation(Location location) {
        return api.getPosition(location);
    }

    @Override
    public @NotNull String getPluginName() {
        return "HuskClaims";
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        Map<ClaimWorld, List<Claim>> claims = api.getUserClaims(OnlineUser.of(player.getUniqueId(), player.getName()));
        List<HuskClaimsClaimInfo> list = new ArrayList<>();

        for (ClaimWorld cw : claims.keySet()) {
            for (Claim claim : claims.get(cw)) {
                list.add(new HuskClaimsClaimInfo(cw, claim));
            }
        }

        return list;
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return api.isWorldClaimable(wrapWorld(location.getWorld())) && api.isClaimAt(wrapLocation(location));
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
        Optional<ClaimWorld> ocw = HuskClaimsAPI.getInstance().getClaimWorld(wrapWorld(location.getWorld()));

        if (!api.isWorldClaimable(wrapWorld(location.getWorld())) || !ocw.isPresent()) {
            return null;
        }

        Optional<Claim> claim = api.getClaimAt(wrapLocation(location));
        ClaimWorld cw = ocw.get();
        return claim.map(c -> new HuskClaimsClaimInfo(cw, c)).orElse(null);
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        OperationTypeRegistry reg = api.getOperationTypeRegistry();
        OperationType type = reg.createOperationType(Key.key(flag.namespace(), flag.id()));
        reg.registerOperationType(type);
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return true;
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        Optional<OperationType> operationType = OperationType.get(flag);
        if (!operationType.isPresent()) {
            return FlagStates.UNSUPPORTED;
        }

        Optional<ClaimWorld> cw = api.getClaimWorld(wrapWorld(Bukkit.getWorld(world)));
        if (!cw.isPresent()) {
            return FlagStates.WORLD_NOT_FOUND;
        }

        return FlagStates.fromBoolean(cw.get().getWildernessFlags().contains(operationType.get()));
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        if (flag.getForHuskClaims() == null) {
            return FlagStates.UNSUPPORTED;
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

    @EventHandler
    public void onCreated(BukkitCreateClaimEvent e) {
        Region.Point center = e.getRegion().getCenter();
        Optional<Claim> claim = e.getClaimWorld().getClaimAt(center);

        if (!claim.isPresent()) {
            return;
        }

        HuskClaimsClaimInfo info = new HuskClaimsClaimInfo(e.getClaimWorld(), claim.get());

        ProtectorAPI.getHandlers(RangeCreateHandler.class).forEach(a -> a.onCreate(this, info));
    }

    @EventHandler
    public void onDelete(BukkitDeleteClaimEvent e) {
        HuskClaimsClaimInfo info = new HuskClaimsClaimInfo(e.getClaimWorld(), e.getClaim());

        ProtectorAPI.getHandlers(RangeDeleteHandler.class).forEach(a -> a.onDelete(this, info));
    }

    static org.bukkit.World getBukkitWorldByClaimWorld(ClaimWorld claimWorld) {
        Map<ServerWorld, ClaimWorld> worlds;
        try {
            worlds = HuskClaimsAPI.getInstance().getGlobalClaimWorlds().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        Optional<ServerWorld> sw = worlds.keySet().stream().filter(w -> worlds.get(w).equals(claimWorld)).findFirst();
        return sw.map(serverWorld -> BukkitHuskClaims.Adapter.adapt(serverWorld.world())).orElse(null);
    }
}
