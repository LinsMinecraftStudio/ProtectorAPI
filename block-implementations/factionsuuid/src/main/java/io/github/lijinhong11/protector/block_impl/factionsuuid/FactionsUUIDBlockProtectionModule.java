package io.github.lijinhong11.protector.block_impl.factionsuuid;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FactionsUUIDBlockProtectionModule implements IBlockProtectionModule {
    public FactionsUUIDBlockProtectionModule() {
    }

    @Override
    public @NotNull String getPluginName() {
        return "FactionsUUID";
    }

    @Override
    public boolean isProtected(Location block) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(block));

        return faction != null;
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(block));
        if (faction == null) {
            return true;
        } else {
            return faction.getIntId() == FPlayers.getInstance().getByPlayer(player).getFaction().getIntId();
        }
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(block));
        if (faction == null) {
            return true;
        } else {
            return faction.getIntId() == FPlayers.getInstance().getByPlayer(player).getFaction().getIntId();
        }
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(block));
        if (faction == null) {
            return true;
        } else {
            return faction.getIntId() == FPlayers.getInstance().getByPlayer(player).getFaction().getIntId();
        }
    }
}
