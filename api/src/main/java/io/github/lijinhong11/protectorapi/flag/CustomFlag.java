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
package io.github.lijinhong11.protectorapi.flag;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom flag
 */
public record CustomFlag(
        @NotNull Plugin plugin,
        @NotNull String namespace,
        @NotNull String id,
        boolean defaultValue,
        @Nullable String displayName,
        @Nullable String description) {
    /**
     * The custom flag object
     *
     * @param plugin       the plugin
     * @param namespace    the namespace for verify
     * @param id           the flag ID
     * @param defaultValue the default value
     * @param displayName  the display name of the flag (optional)
     * @param description  the description about the flag (optional)
     */
    public CustomFlag {}

    /**
     * Gets the plugin
     *
     * @return the plugin
     */
    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String description() {
        return description;
    }
}
