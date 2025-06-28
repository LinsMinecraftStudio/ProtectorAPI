package io.github.lijinhong11.protector.api;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
}
