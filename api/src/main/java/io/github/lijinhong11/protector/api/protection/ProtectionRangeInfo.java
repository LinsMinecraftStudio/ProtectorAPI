package io.github.lijinhong11.protector.api.protection;

import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.IFlagState;
import java.util.List;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProtectionRangeInfo {
    /**
     * Get all flags
     *
     * @return all flags (the map is not modifiable)
     */
    @NotNull Map<String, IFlagState<?>> getFlags();

    /**
     * Get flag state
     * @param flag flag name
     * @return flag state
     */
    IFlagState<?> getFlagState(@NotNull String flag);

    /**
     * Get flag state
     * @param flag flag name
     * @param player player
     * @return flag state
     */
    IFlagState<?> getFlagState(@NotNull String flag, OfflinePlayer player);

    /**
     * Get flag state
     * @param flag flag
     * @return flag state
     */
    IFlagState<?> getFlagState(@NotNull CommonFlags flag);

    /**
     * Get flag state
     * @param flag flag
     * @param player player
     * @return flag state
     */
    IFlagState<?> getFlagState(@NotNull CommonFlags flag, OfflinePlayer player);

    /**
     * Get all admins
     * @return all admins
     */
    List<OfflinePlayer> getAdmins();

    /**
     * Get all members
     * @return all members
     */
    List<OfflinePlayer> getMembers();

    /**
     * Get owner
     * @return owner
     */
    @Nullable OfflinePlayer getOwner();
}
