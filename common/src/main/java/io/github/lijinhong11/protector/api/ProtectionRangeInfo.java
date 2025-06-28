package io.github.lijinhong11.protector.api;

import io.github.lijinhong11.protector.api.flag.FlagState;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface ProtectionRangeInfo {
    Map<String, FlagState> getFlags();

    FlagState getFlagState(String flag);

    FlagState getFlagState(String flag, OfflinePlayer player);

    List<OfflinePlayer> getAdmins();

    @Nullable OfflinePlayer getOwner();
}
