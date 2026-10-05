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
package io.github.lijinhong11.protector.block_impl.husktowns;

import io.github.lijinhong11.protectorapi.flag.CustomFlag;
import io.github.lijinhong11.protectorapi.flag.FlagRegisterable;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import net.kyori.adventure.key.Key;
import net.william278.husktowns.api.BukkitHuskTownsAPI;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HuskTownsBlockProtectionModule implements IBlockProtectionModule, FlagRegisterable {
    private final BukkitHuskTownsAPI api = BukkitHuskTownsAPI.getInstance();

    @Override
    public @NotNull String getPluginName() {
        return "HuskTowns";
    }

    @Override
    public boolean isProtected(Location block) {
        return api.isClaimAt(api.getPosition(block));
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return api.isOperationAllowed(api.getOnlineUser(player), OperationType.BLOCK_BREAK, api.getPosition(block));
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return api.isOperationAllowed(api.getOnlineUser(player), OperationType.BLOCK_PLACE, api.getPosition(block));
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return api.isOperationAllowed(api.getOnlineUser(player), OperationType.BLOCK_INTERACT, api.getPosition(block));
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        Key key = Key.key(flag.namespace(), flag.id());
        OperationType ot = api.getOperationTypeRegistry().createOperationType(key);

        api.getOperationTypeRegistry().registerOperationType(ot);
    }
}
