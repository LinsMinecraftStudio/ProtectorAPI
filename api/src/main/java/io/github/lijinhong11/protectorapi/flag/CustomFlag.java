package io.github.lijinhong11.protectorapi.flag;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom flag
 */
public record CustomFlag(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String id, boolean defaultValue,
                         @Nullable String displayName, @Nullable String description) {
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
    public CustomFlag {
    }

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
