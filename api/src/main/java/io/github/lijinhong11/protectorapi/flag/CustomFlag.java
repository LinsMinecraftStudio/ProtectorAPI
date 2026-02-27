package io.github.lijinhong11.protectorapi.flag;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom flag
 */
public class CustomFlag {
    private final @NotNull Plugin plugin;
    private final @NotNull String namespace;
    private final @NotNull String id;
    private final boolean defaultValue;
    private final @Nullable String displayName;
    private final @Nullable String description;

    /**
     * The custom flag object
     * @param plugin the plugin
     * @param namespace the namespace for verify
     * @param id the flag ID
     * @param defaultValue the default value
     * @param displayName the display name of the flag (optional)
     * @param description the description about the flag (optional)
     */
    public CustomFlag(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String id, boolean defaultValue, @Nullable String displayName, @Nullable String description) {
        this.plugin = plugin;
        this.namespace = namespace;
        this.id = id;
        this.defaultValue = defaultValue;
        this.displayName = displayName;
        this.description = description;
    }

    public Plugin plugin() {
        return plugin;
    }

    public String namespace() {
        return namespace;
    }

    public String id() {
        return id;
    }

    public boolean defaultValue() {
        return defaultValue;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }
}
