package io.github.lijinhong11.protector.api;

import com.google.common.base.Preconditions;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.flag.CustomFlag;
import io.github.lijinhong11.protector.api.flag.FlagRegisterable;
import io.github.lijinhong11.protector.api.protection.FakeEventMaker;
import io.github.lijinhong11.protector.api.protection.IProtectionModule;
import io.github.lijinhong11.protector.api.protection.IProtectionRangeInfo;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProtectorAPI {
    private static final Set<IProtectionModule> modules;
    private static final Set<IBlockProtectionModule> blockModules;

    private static ProtectionAPIPlugin pluginHost;

    static {
        modules = new CopyOnWriteArraySet<>();
        blockModules = new CopyOnWriteArraySet<>();
    }

    /**
     * Register the protection module
     * @param module the protection module
     */
    public static void register(IProtectionModule module) {
        Preconditions.checkNotNull(module, "module cannot be null");
        Preconditions.checkArgument(!modules.contains(module), "module already registered");

        modules.add(module);
    }

    /**
     * Register the block protection module
     * @param module the block protection module
     */
    public static void register(IBlockProtectionModule module) {
        Preconditions.checkNotNull(module, "block module cannot be null");
        Preconditions.checkArgument(!blockModules.contains(module), "block module already registered");

        blockModules.add(module);
    }

    /**
     * Set the plugin instance
     * @param plugin the plugin instance
     * @apiNote this is not for you
     */
    @ApiStatus.Internal
    public static void setPluginHost(ProtectionAPIPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin cannot be null");
        Preconditions.checkArgument(pluginHost == null, "plugin host already set");

        pluginHost = plugin;
    }

    /**
     * Get the plugin instance
     * @return the plugin instance
     */
    public static ProtectionAPIPlugin getPluginHost() {
        return pluginHost;
    }

    /**
     * Get the first available protection module
     * @return the first available protection module
     */
    @Nullable public static IProtectionModule getFirstAvailableModule() {
        return modules.toArray(IProtectionModule[]::new)[0];
    }

    /**
     * Find the protection module by plugin name
     * @param pluginName the plugin name
     * @return the protection module
     */
    @Nullable public static IProtectionModule getModuleByPluginName(String pluginName) {
        for (IProtectionModule module : modules) {
            if (module.getPluginName().equalsIgnoreCase(pluginName)) {
                return module;
            }
        }

        return null;
    }

    /**
     * Register a custom flag.
     */
    public static void registerFlag(CustomFlag flag) {
        for (IProtectionModule module : modules) {
            if (module instanceof FlagRegisterable fr) {
                fr.registerFlag(flag);
            }
        }

        for (IBlockProtectionModule module : blockModules) {
            if (module instanceof FlagRegisterable fr) {
                fr.registerFlag(flag);
            }
        }
    }

    /**
     * Get all available protection modules.
     * @return a list contains all available protection modules.
     */
    public static Collection<IProtectionModule> getAllAvailableProtectionModules() {
        return modules;
    }

    /**
     * Get all available block protection modules.
     * @return a list contains all available protection modules.
     */
    public static Collection<IBlockProtectionModule> getAllAvailableBlockProtectionModules() {
        return blockModules;
    }

    /**
     * Find the protection module that protects the protection range
     * @param location the location in the protection range
     * @return the protection module
     */
    @Nullable public static IProtectionModule findModule(Location location) {
        for (IProtectionModule module : modules) {
            if (module.isInProtectionRange(location)) {
                return module;
            }
        }

        return null;
    }

    /**
     * Find the block protection module that protects the block
     * @param p the player
     * @param block the block
     * @return the block protection module
     */
    @Nullable public static IBlockProtectionModule findBlockModule(Player p, Location block) {
        for (IBlockProtectionModule module : blockModules) {
            if (module.isProtected(p, block)) {
                return module;
            }
        }

        return null;
    }

    /**
     * Check the event is fake
     * @param event the event
     * @return true if the event is fake, false otherwise
     */
    public static boolean isEventFake(@NotNull Event event) {
        boolean fake = false;
        for (IBlockProtectionModule module : blockModules) {
            if (module instanceof FakeEventMaker fem) {
                fake = fem.isEventFake(event);
                if (fake) {
                    return true;
                }
            }
        }

        return fake;
    }

    /**
     * Check if the player can break a block
     * @param player the player
     * @return true if the player can break a block, false otherwise
     */
    public static boolean allowBreak(Player player) {
        Location location = player.getLocation();
        IProtectionModule module = findModule(location); // proved protection range isn't null
        if (module == null) {
            for (IProtectionModule m2 : modules) {
                if (m2.isSupportGlobalFlags()) {
                    return (boolean) Objects.requireNonNull(m2.getGlobalFlag(
                                    CommonFlags.BREAK, location.getWorld().getName()))
                            .value();
                }
            }

            return true;
        }

        IProtectionRangeInfo info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        return (boolean) info.getFlagState(CommonFlags.BREAK, player).value();
    }

    /**
     * Check if the player can break a block
     * @param player the player
     * @param block the block
     * @return true if the player can break a block, false otherwise
     */
    public static boolean allowBreak(Player player, Location block) {
        IBlockProtectionModule module = findBlockModule(player, block);
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

    /**
     * Check if the player can place a block
     * @param player the player
     * @return true if the player can place a block, false otherwise
     */
    public static boolean allowPlace(Player player) {
        Location location = player.getLocation();
        IProtectionModule module = findModule(location);
        if (module == null) {
            for (IProtectionModule m2 : modules) {
                if (m2.isSupportGlobalFlags()) {
                    return (boolean) Objects.requireNonNull(m2.getGlobalFlag(
                                    CommonFlags.PLACE, location.getWorld().getName()))
                            .value();
                }
            }

            return true;
        }

        IProtectionRangeInfo info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        return (boolean) info.getFlagState(CommonFlags.PLACE, player).value();
    }

    /**
     * Check if the player can place a block
     * @param player the player
     * @param block the block
     * @return true if the player can place a block, false otherwise
     */
    public static boolean allowPlace(Player player, Location block) {
        IBlockProtectionModule module = findBlockModule(player, block);
        if (allowPlace(player)) {
            if (module == null) {
                return true;
            }

            if (!module.allowPlace(player, block)) {
                return false;
            }
        }

        return false;
    }

    /**
     * Check if the player can interact with a block
     * @param player the player
     * @return true if the player can interact with a block
     */
    public static boolean allowInteract(Player player) {
        Location location = player.getLocation();
        IProtectionModule module = findModule(location);
        if (module == null) {
            for (IProtectionModule m2 : modules) {
                if (m2.isSupportGlobalFlags()) {
                    return (boolean) Objects.requireNonNull(m2.getGlobalFlag(
                                    CommonFlags.INTERACT, location.getWorld().getName()))
                            .value();
                }
            }

            return true;
        }

        IProtectionRangeInfo info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        return (boolean) info.getFlagState(CommonFlags.INTERACT, player).value();
    }

    /**
     * Check if the player can interact with the block
     * @param player the player
     * @param block  the block
     * @return true if the player can interact with the block
     */
    public static boolean allowInteract(Player player, Location block) {
        IBlockProtectionModule module = findBlockModule(player, block);
        if (allowInteract(player)) {
            if (module == null) {
                return true;
            }

            if (!module.allowInteract(player, block)) {
                return false;
            }
        }

        return false;
    }
}
