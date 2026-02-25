package io.github.lijinhong11.protector.impl.bentobox;

import io.github.lijinhong11.protectorapi.convertions.FlagMap;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IProtectionRangeInfo;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

public class BentoBoxIslandInfo implements IProtectionRangeInfo {
    private final Island island;

    public BentoBoxIslandInfo(Island island) {
        this.island = island;
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
        if (theFlagOptional.isEmpty()) {
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
                .toList();
    }

    @Override
    public List<OfflinePlayer> getMembers() {
        return island.getMembers().keySet().stream()
                .map(Bukkit::getOfflinePlayer)
                .toList();
    }

    @Override
    public @Nullable OfflinePlayer getOwner() {
        return island.getOwner() == null ? null : Bukkit.getOfflinePlayer(island.getOwner());
    }
}
