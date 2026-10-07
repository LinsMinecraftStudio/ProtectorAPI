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
package io.github.lijinhong11.protectorapi.protection;

/**
 * Operations whose thread safety must be verified separately for each adapter.
 */
public enum ProtectionCheck {
    /**
     * Location-based protection lookup, including retrieving range information.
     */
    LOOKUP,
    /**
     * Range flag reads with a player, or block break/place/interact permission checks.
     */
    FLAGS,
    /**
     * Global flag support and global flag reads (not flag registration or mutation).
     */
    GLOBAL_FLAGS
}
