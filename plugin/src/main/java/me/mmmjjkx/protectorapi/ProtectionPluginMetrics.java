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
