package io.github.lijinhong11.protector.impl.bentobox;

import io.github.lijinhong11.protector.api.convertions.FlagMap;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.FlagState;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import io.github.lijinhong11.protector.api.protection.ProtectionRangeInfo;
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

public class BentoBoxIslandInfo implements ProtectionRangeInfo {
    private final Island island;

    public BentoBoxIslandInfo(Island island) {
        this.island = island;
    }

    @Override
    public @NotNull Map<String, IFlagState<?>> getFlags() {
        FlagMap flagMap = new FlagMap(island.getFlags(), i -> FlagState.fromBoolean(i >= 0));
        return Collections.unmodifiableMap(flagMap);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull String flag) {
        return getFlagState(flag, null);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player) {
        FlagsManager manager = BentoBox.getInstance().getFlagsManager();
        Optional<Flag> theFlagOptional = manager.getFlag(flag);
        if (theFlagOptional.isEmpty()) {
            return FlagState.UNSUPPORTED;
        }

        Flag theFlag = theFlagOptional.get();

        if (player == null) {
            return FlagState.fromBoolean(island.isAllowed(theFlag));
        }

        User user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());
        return FlagState.fromBoolean(island.isAllowed(user, theFlag));
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull CommonFlags flag) {
        return getFlagState(flag, null);
    }

    @Override
    public IFlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player) {
        if (flag.getForBentoBox() == null) {
            return FlagState.UNSUPPORTED;
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
