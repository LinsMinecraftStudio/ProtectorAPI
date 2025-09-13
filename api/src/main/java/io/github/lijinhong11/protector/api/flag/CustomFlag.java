package io.github.lijinhong11.protector.api.flag;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The custom flag object
 * @param plugin the plugin
 * @param namespace the namespace for verify
 * @param id the flag ID
 * @param defaultValue the default value
 * @param displayName the display name of the flag (optional)
 * @param description the description about the flag (optional)
 */
public record CustomFlag(
        @NotNull Plugin plugin,
        @NotNull String namespace,
        @NotNull String id,
        boolean defaultValue,
        @Nullable String displayName,
        @Nullable String description) {}
