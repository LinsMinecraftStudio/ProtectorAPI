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
package io.github.lijinhong11.protector.block_impl.nobuildplus;

import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import p1xel.nobuildplus.API.NBPAPI;
import p1xel.nobuildplus.Flags;
import p1xel.nobuildplus.NoBuildPlus;

public class NoBuildPlusBlockProtectionModule implements IBlockProtectionModule {
    private final NBPAPI api = NoBuildPlus.getInstance().getAPI();

    @Override
    public @NotNull String getPluginName() {
        return "NoBuildPlus";
    }

    @Override
    public boolean isProtected(Location block) {
        return api.isWorldEnabled(block.getWorld().getName());
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return api.canExecute(block.getWorld().getName(), Flags.destroy);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return api.canExecute(block.getWorld().getName(), Flags.build);
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return api.canExecute(block.getWorld().getName(), Flags.use);
    }
}
