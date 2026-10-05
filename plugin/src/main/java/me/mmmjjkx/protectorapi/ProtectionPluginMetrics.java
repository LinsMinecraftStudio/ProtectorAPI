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
package me.mmmjjkx.protectorapi;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

final class ProtectionPluginMetrics {
    private ProtectionPluginMetrics() {}

    static Map<String, Integer> countPluginNames(
            Collection<String> protectionPlugins, Collection<String> blockProtectionPlugins) {
        Map<String, Integer> plugins = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        addPluginNames(plugins, protectionPlugins);
        addPluginNames(plugins, blockProtectionPlugins);
        return plugins.isEmpty() ? Collections.emptyMap() : plugins;
    }

    private static void addPluginNames(Map<String, Integer> plugins, Collection<String> names) {
        for (String name : names) {
            if (name != null && !name.trim().isEmpty()) {
                plugins.put(name, 1);
            }
        }
    }
}
