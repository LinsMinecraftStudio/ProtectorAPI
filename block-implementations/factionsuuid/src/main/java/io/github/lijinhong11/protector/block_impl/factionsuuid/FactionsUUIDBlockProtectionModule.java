package io.github.lijinhong11.protector.block_impl.factionsuuid;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FactionsUUIDBlockProtectionModule implements IBlockProtectionModule {
    private final FPlayers api;

    public FactionsUUIDBlockProtectionModule() {
        api = FPlayers.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "FactionsUUID";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(block));

        if (faction == null || faction.getIntId() == 0) {
            return true;
        } else {
            return faction.getIntId() == api.getByPlayer(player).getFaction().getIntId();
        }
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return false;
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return false;
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return false;
    }
}
