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
package io.github.lijinhong11.protector.impl.bentobox;

import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.objects.FlagMap;
import io.github.lijinhong11.protectorapi.objects.WorldCollection;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.FlagsManager;
import world.bentobox.bentobox.managers.RanksManager;

public class BentoBoxIslandInfo implements IProtectionRange {
    private final Island island;

    public BentoBoxIslandInfo(Island island) {
        this.island = island;
    }

    @Override
    public @NotNull String getId() {
        return island.getUniqueId();
    }

    @Override
    public @NotNull String getDisplayName() {
        if (island.getName() == null) {
            return "NAME_NULL";
        }

        return island.getName();
    }

    @Override
    public @NotNull WorldCollection getWorld() {
        return new WorldCollection(island.getWorld(), island.getNetherWorld(), island.getEndWorld());
    }

    @Override
    public @NotNull Map<String, FlagState<?>> getFlags() {
        FlagMap flagMap = new FlagMap(island.getFlags(), i -> FlagStates.fromBoolean(i >= 0));
        return Collections.unmodifiableMap(flagMap);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        FlagsManager manager = BentoBox.getInstance().getFlagsManager();
        Optional<Flag> theFlagOptional = manager.getFlag(flag);
        if (!theFlagOptional.isPresent()) {
            return FlagStates.UNSUPPORTED;
        }

        Flag theFlag = theFlagOptional.get();

        if (player == null) {
            return FlagStates.fromBoolean(island.isAllowed(theFlag));
        }

        User user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());
        return FlagStates.fromBoolean(island.isAllowed(user, theFlag));
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag, null);
    }

    @Override
    public FlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        if (flag.getForBentoBox() == null) {
            return FlagStates.UNSUPPORTED;
        }

        return getFlagState(flag.getForBentoBox(), player);
    }

    @Override
    public List<OfflinePlayer> getAdmins() {
        return getMembers().stream()
                .filter(m -> {
                    User user = BentoBox.getInstance().getPlayers().getUser(m.getUniqueId());
                    return island.getRank(user) >= RanksManager.SUB_OWNER_RANK;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return island.getMembers().keySet().stream()
                .map(Bukkit::getOfflinePlayer)
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return island.getOwner() == null ? null : Bukkit.getOfflinePlayer(island.getOwner());
    }
}
