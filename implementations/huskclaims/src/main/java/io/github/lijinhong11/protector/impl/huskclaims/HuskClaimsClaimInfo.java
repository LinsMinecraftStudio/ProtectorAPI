package io.github.lijinhong11.protector.impl.huskclaims;

import io.github.lijinhong11.protectorapi.objects.FlagMap;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import net.william278.huskclaims.api.HuskClaimsAPI;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.claim.ClaimWorld;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import net.william278.huskclaims.position.ServerWorld;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class HuskClaimsClaimInfo implements IProtectionRange {
    private final ClaimWorld world;
    private final Claim claim;

    public HuskClaimsClaimInfo(@NotNull ClaimWorld world, Claim claim) {
        this.world = world;
        this.claim = claim;
    }

    @Override
    public @NotNull String getId() {
        return "";
    }

    @Override
    public @NotNull String getDisplayName() {
        return "";
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        return new WorldCollection(HuskClaimsProtectionModule.getBukkitWorldByClaimWorld(world));
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        FlagMap map = new FlagMap();
        for (OperationType type : OperationType.getRegistered()) {
            map.put(
                    type.asMinimalString(),
                    FlagStates.fromBoolean(claim.getDefaultFlags().contains(type)));
        }

        return map;
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        Optional<OperationType> type = OperationType.get(flag);
        if (!type.isPresent()) {
            return FlagStates.UNSUPPORTED;
        }

        return FlagStates.fromBoolean(claim.getDefaultFlags().contains(type.get()));
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        if (flag.getForHuskClaims() == null) {
            return FlagStates.UNSUPPORTED;
        }

        return getFlagState(flag.getForHuskClaims(), player);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return new ArrayList<>();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return claim.getTrustedUsers().keySet().stream()
                .map(Bukkit::getOfflinePlayer)
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return claim.getOwner().map(Bukkit::getOfflinePlayer).orElse(null);
    }
}
