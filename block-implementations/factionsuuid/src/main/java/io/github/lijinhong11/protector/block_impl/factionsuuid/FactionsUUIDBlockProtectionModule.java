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
    public FactionsUUIDBlockProtectionModule() {}

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
            return faction.getIntId()
                    == FPlayers.getInstance().getByPlayer(player).getFaction().getIntId();
        }
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(block));
        if (faction == null) {
            return true;
        } else {
            return faction.getIntId()
                    == FPlayers.getInstance().getByPlayer(player).getFaction().getIntId();
        }
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(block));
        if (faction == null) {
            return true;
        } else {
            return faction.getIntId()
                    == FPlayers.getInstance().getByPlayer(player).getFaction().getIntId();
        }
    }
}
