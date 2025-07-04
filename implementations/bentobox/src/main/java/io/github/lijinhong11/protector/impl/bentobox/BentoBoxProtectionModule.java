package io.github.lijinhong11.protector.impl.bentobox;

import io.github.lijinhong11.protector.api.IProtectionModule;
import io.github.lijinhong11.protector.api.ProtectionRangeInfo;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandWorldManager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BentoBoxProtectionModule implements IProtectionModule {
    private final BentoBox bentoBox;

    public BentoBoxProtectionModule() {
        this.bentoBox = BentoBox.getInstance();
    }

    @Override
    public String getPluginName() {
        return "BentoBox";
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
        Collection<Island> islands = bentoBox.getIslands().getIslands();
        if (islands.isEmpty()) {
            return List.of();
        }


        for (Island island : islands) {
            if (island.getMembers().containsKey(player.getUniqueId())) {
                return Collections.singletonList(new BentoBoxIslandInfo(island));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isInProtectionRange(Location location) {
        return false;
    }

    @Override
    public @Nullable ProtectionRangeInfo getProtectionRangeInfo(@NotNull Location location) {
        Optional<Island> island = bentoBox.getIslands().getIslandAt(location);
        return island.map(BentoBoxIslandInfo::new).orElse(null);
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return false;
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {
        throw new UnsupportedOperationException();
    }
}
