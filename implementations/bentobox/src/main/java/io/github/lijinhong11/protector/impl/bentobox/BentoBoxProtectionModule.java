package io.github.lijinhong11.protector.impl.bentobox;

import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.handlers.RangeCreateHandler;
import io.github.lijinhong11.protectorapi.handlers.RangeDeleteHandler;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.island.IslandCreatedEvent;
import world.bentobox.bentobox.api.events.island.IslandDeletedEvent;
import world.bentobox.bentobox.database.objects.Island;

import java.util.*;

public class BentoBoxProtectionModule implements IProtectionModule, Listener {
    private final BentoBox bentoBox;

    public BentoBoxProtectionModule() {
        this.bentoBox = BentoBox.getInstance();

        Bukkit.getPluginManager().registerEvents(this, ProtectorAPI.getPluginHost());
    }

    @Override
    public @NotNull String getPluginName() {
        return "BentoBox";
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        Collection<Island> islands = bentoBox.getIslands().getIslands();
        if (islands.isEmpty()) {
            return Collections.emptyList();
        }

        for (Island island : islands) {
            if (island.getMembers().containsKey(player.getUniqueId())) {
                return Collections.singletonList(new BentoBoxIslandInfo(island));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return false;
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
        Optional<Island> island = bentoBox.getIslands().getIslandAt(location);
        return island.map(BentoBoxIslandInfo::new).orElse(null);
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return false;
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
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

    @EventHandler
    public void onCreated(IslandCreatedEvent e) {
        BentoBoxIslandInfo info = new BentoBoxIslandInfo(e.getIsland());

        ProtectorAPI.getHandlers(RangeCreateHandler.class).forEach(a -> a.onCreate(this, info));
    }

    @EventHandler
    public void onDelete(IslandDeletedEvent e) {
        BentoBoxIslandInfo info = new BentoBoxIslandInfo(e.getIsland());

        ProtectorAPI.getHandlers(RangeDeleteHandler.class).forEach(a -> a.onDelete(this, info));
    }
}
