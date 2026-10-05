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
package io.github.lijinhong11.protector.block_impl.lands;

import io.github.lijinhong11.protectorapi.flag.CustomFlag;
import me.angeschossen.lands.api.flags.DefaultStateFlag;
import me.angeschossen.lands.api.flags.enums.FlagModule;
import org.jetbrains.annotations.NotNull;

public class ProtectorAPIManagedFlag extends DefaultStateFlag<ProtectorAPIManagedFlag> {
    private final CustomFlag object;

    public ProtectorAPIManagedFlag(CustomFlag flag) {
        super(flag.plugin(), Target.PLAYER, flag.displayName() == null ? flag.id() : flag.displayName(), true, true);

        object = flag;
    }

    @Override
    protected ProtectorAPIManagedFlag self() {
        return this;
    }

    @Override
    public @NotNull String getTogglePerm() {
        return object.plugin().getName().toLowerCase() + ".flag_setting." + object.id();
    }

    @Override
    public @NotNull String getTogglePermission() {
        return getTogglePerm();
    }

    @Override
    public @NotNull FlagModule getModule() {
        return FlagModule.PLAYER;
    }
}
