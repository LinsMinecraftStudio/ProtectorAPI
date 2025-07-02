package io.github.lijinhong11.protector.api;

import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IProtectionModule {
    /**
     * Get the name of the corresponding plugin
     * @return the name of the corresponding plugin
     */
    String getPluginName();

    /**
     * Check if the player is in the protection range
     * @param player the player
     * @return true if the player is in the protection range
     */
    boolean isInProtectionRange(Player player);

    /**
     * Get the protection range info of the player's location
     * @param player the player
     * @return the protection range info
     */
    @Nullable ProtectionRangeInfo getProtectionRangeInfo(Player player);

    /**
     * Get all protection range info that the player owns
     *
     * @param player the player
     * @return the protection range info
     */
    List<? extends ProtectionRangeInfo> getProtectionRangeInfos(OfflinePlayer player);

    /**
     * Check if the location is in the protection range
     * @param location the location
     * @return true if the location is in the protection range
     */
    boolean isInProtectionRange(Location location);

    /**
     * Get the protection range info of the location
     * @param location the location
     * @return the protection range info (if the location is not in the protection range, return null)
     */
    @Nullable ProtectionRangeInfo getProtectionRangeInfo(@Nullable Location location);

    /**
     * Whether the module supports global flags
     * @return true if the module supports global flags
     */
    boolean isSupportGlobalFlags();

    /**
     * Get the global flag
     *
     * @param flag  the flag
     * @param world the world
     * @return the global flag
     * @throws UnsupportedOperationException if the module does not support global flags
     */
    IFlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world);

    /**
     * Get the global flag
     * @param flag the flag
     * @param world the world
     * @return the global flag
     * @throws UnsupportedOperationException if the module does not support global flags
     */
    IFlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world);

    /**
     * Set the global flag
     * @param flag the flag
     * @throws UnsupportedOperationException if the module does not support global flags
     */
    void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value);

    /**
     * Set the global flag
     *
     * @param world the world
     * @param flag the flag
     * @throws UnsupportedOperationException if the module does not support global flags
     */
    void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value);
}
