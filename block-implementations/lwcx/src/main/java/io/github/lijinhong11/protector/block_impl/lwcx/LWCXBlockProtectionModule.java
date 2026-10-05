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
package io.github.lijinhong11.protector.block_impl.lwcx;

import com.griefcraft.lwc.LWC;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LWCXBlockProtectionModule implements IBlockProtectionModule {
    private final LWC lwc;

    public LWCXBlockProtectionModule() {
        this.lwc = LWC.getInstance();
    }

    @Override
    public @NotNull String getPluginName() {
        return "LWC";
    }

    @Override
    public boolean isProtected(Location block) {
        return lwc.isProtectable(block.getBlock());
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return check(player, block);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return check(player, block);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return check(player, block);
    }

    private boolean check(Player p, Location loc) {
        if (!lwc.isProtectable(loc.getBlock())) {
            return true;
        } else if (lwc.getProtectionCache().getProtection(loc.getBlock()) == null) {
            return true;
        }

        return lwc.canAccessProtection(p, loc.getBlock());
    }
}
