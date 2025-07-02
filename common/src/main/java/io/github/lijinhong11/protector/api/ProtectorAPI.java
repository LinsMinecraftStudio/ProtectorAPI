package io.github.lijinhong11.protector.api;

import com.google.common.base.Preconditions;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProtectorAPI {
    private static final List<IProtectionModule> modules;

    static {
        modules = new CopyOnWriteArrayList<>();
    }

    public static void register(IProtectionModule module) {
        Preconditions.checkNotNull(module, "module cannot be null");
        Preconditions.checkArgument(!modules.contains(module), "module already registered");

        modules.add(module);
    }

    @Nullable
    public static IProtectionModule findModule(Location location) {
        for (IProtectionModule module : modules) {
            if (module.isInProtectionRange(location)) {
                return module;
            }
        }

        return null;
    }

    public static boolean allowBreak(Player player) {
        Location location = player.getLocation();
        IProtectionModule module = findModule(location);
        if (module == null) {
            for (IProtectionModule m2 : modules) {
                if (m2.isSupportGlobalFlags()) {
                    return (boolean) Objects.requireNonNull(m2.getGlobalFlag(CommonFlags.BREAK, location.getWorld().getName())).getValue();
                }
            }

            return true;
        }

        return (boolean) Objects.requireNonNull(module.getProtectionRangeInfo(location)).getFlagState(CommonFlags.BREAK).getValue();
    }
}
