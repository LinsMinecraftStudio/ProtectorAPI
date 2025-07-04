package io.github.lijinhong11.protector.api;

import com.google.common.base.Preconditions;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProtectorAPI {
    private static final List<IProtectionModule> modules;
    private static final List<IBlockProtectionModule> blockModules;

    static {
        modules = new CopyOnWriteArrayList<>();
        blockModules = new CopyOnWriteArrayList<>();
    }

    public static void register(IProtectionModule module) {
        Preconditions.checkNotNull(module, "module cannot be null");
        Preconditions.checkArgument(!modules.contains(module), "module already registered");

        modules.add(module);
    }

    public static void register(IBlockProtectionModule module) {
        Preconditions.checkNotNull(module, "block module cannot be null");
        Preconditions.checkArgument(!blockModules.contains(module), "block module already registered");

        blockModules.add(module);
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

    @Nullable
    public static IBlockProtectionModule findBlockModule(Location block) {
        for (IBlockProtectionModule module : blockModules) {
            if (module.isProtected(block)) {
                return module;
            }
        }

        return null;
    }

    //break is all supported, so no worries
    public static boolean allowBreak(Player player) {
        Location location = player.getLocation();
        IProtectionModule module = findModule(location); //proved protection range isn't null
        if (module == null) {
            for (IProtectionModule m2 : modules) {
                if (m2.isSupportGlobalFlags()) {
                    return (boolean) Objects.requireNonNull(m2.getGlobalFlag(CommonFlags.BREAK, location.getWorld().getName())).getValue();
                }
            }

            return true;
        }

        ProtectionRangeInfo info = module.getProtectionRangeInfo(player);
        return (Boolean) info.getFlagState(CommonFlags.BREAK, player).getValue();
    }

    //break is all supported, so no worries
    public static boolean allowBreak(Player player, Location block) {
        IBlockProtectionModule module = findBlockModule(block);
        if (allowBreak(player)) {
            if (module == null) {
                return true;
            }

            if (!module.allowBreak(player, block)) {
                return false;
            }
        }

        return false;
    }
}
