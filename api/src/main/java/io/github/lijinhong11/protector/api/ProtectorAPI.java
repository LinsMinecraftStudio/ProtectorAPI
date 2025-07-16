package io.github.lijinhong11.protector.api;

import com.google.common.base.Preconditions;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import io.github.lijinhong11.protector.api.flag.CommonFlags;
import io.github.lijinhong11.protector.api.protection.FakeEventMaker;
import io.github.lijinhong11.protector.api.protection.IProtectionModule;
import io.github.lijinhong11.protector.api.protection.ProtectionRangeInfo;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProtectorAPI {
    private static final List<IProtectionModule> modules;
    private static final List<IBlockProtectionModule> blockModules;

    private static ProtectionAPIPlugin pluginHost;

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

    public static void setPluginHost(ProtectionAPIPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin cannot be null");
        Preconditions.checkArgument(pluginHost == null, "plugin host already set");

        pluginHost = plugin;
    }

    public static ProtectionAPIPlugin getPluginHost() {
        return pluginHost;
    }

    /**
     * Get the first available protection module
     * @return the first available protection module
     */
    @Nullable public static IProtectionModule getFirstAvailableModule() {
        return modules.get(0);
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
     * Check the event is to check player permission
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
                            .getValue();
                }
            }

            return true;
        }

        ProtectionRangeInfo info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        return (boolean) info.getFlagState(CommonFlags.BREAK, player).getValue();
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
                            .getValue();
                }
            }

            return true;
        }

        ProtectionRangeInfo info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        return (boolean) info.getFlagState(CommonFlags.PLACE, player).getValue();
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
                            .getValue();
                }
            }

            return true;
        }

        ProtectionRangeInfo info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        return (boolean) info.getFlagState(CommonFlags.INTERACT, player).getValue();
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
