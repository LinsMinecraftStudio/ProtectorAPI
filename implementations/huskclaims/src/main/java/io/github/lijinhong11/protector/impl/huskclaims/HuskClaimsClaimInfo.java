/*
 * ProtectorAPI
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.protector.impl.huskclaims;

import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.FlagMap;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.claim.ClaimWorld;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
