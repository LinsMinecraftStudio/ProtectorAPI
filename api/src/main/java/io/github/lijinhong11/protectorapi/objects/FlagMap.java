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
package io.github.lijinhong11.protectorapi.objects;

import io.github.lijinhong11.protectorapi.flag.FlagState;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A map for convert flags fast
 */
public class FlagMap extends HashMap<String, FlagState<?>> {
    public FlagMap() {
        super();
    }

    public FlagMap(int initialCapacity) {
        super(initialCapacity);
    }

    public <T> FlagMap(Map<String, T> map, Function<T, FlagState<?>> converter) {
        super();

        for (Map.Entry<String, T> entry : map.entrySet()) {
            put(entry.getKey(), converter.apply(entry.getValue()));
        }
    }
}
