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

public interface FlagState<T> {
    T value();

    default boolean isBooleanValue() {
        return value() instanceof Boolean;
    }

    /**
     * Converts the value to boolean.
     *
     * @return the boolean value
     * @throws ClassCastException if failed to convert
     */
    default boolean toBooleanOrThrow() {
        if (isBooleanValue()) {
            return (Boolean) value();
        }

        throw new RuntimeException(new ClassCastException());
    }
}
