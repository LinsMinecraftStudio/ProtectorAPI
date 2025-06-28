package io.github.lijinhong11.protector.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
     * Get the protection range info of the player
     * @param player the player
     * @return the protection range info
     */
    @Nullable ProtectionRangeInfo getProtectionRangeInfo(Player player);

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
    @Nullable ProtectionRangeInfo getProtectionRangeInfo(Location location);
}
