/*
 * ProtectorAPI
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.protectorapi;

import com.google.common.base.Preconditions;
import io.github.lijinhong11.protectorapi.flag.*;
import io.github.lijinhong11.protectorapi.handlers.AHandler;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@SuppressWarnings({"unchecked", "unused"})
public class ProtectorAPI {
    private static final Set<IProtectionModule> modules = new CopyOnWriteArraySet<>();
    private static final Set<IBlockProtectionModule> blockModules = new CopyOnWriteArraySet<>();
    private static final List<AHandler> handlers = new ArrayList<>();
    private static final AsyncProtectionChecks asyncChecks = new AsyncProtectionChecks(modules, blockModules);

    private static JavaPlugin pluginHost;

    /**
     * Register the protection module
     *
     * @param module the protection module
     */
    public static void register(IProtectionModule module) {
        Preconditions.checkNotNull(module, "module cannot be null");
        Preconditions.checkArgument(!modules.contains(module), "module already registered");

        modules.add(module);
    }

    /**
     * Register the block protection module
     *
     * @param module the block protection module
     */
    public static void register(IBlockProtectionModule module) {
        Preconditions.checkNotNull(module, "block module cannot be null");
        Preconditions.checkArgument(!blockModules.contains(module), "block module already registered");

        blockModules.add(module);
    }

    public static void registerHandler(AHandler handler) {
        Preconditions.checkNotNull(handler, "handler cannot be null");

        handlers.add(handler);
    }

    /**
     * Get ProtectorAPI's plugin instance
     *
     * @return the plugin instance
     */
    public static JavaPlugin getPluginHost() {
        return pluginHost;
    }

    /**
     * @apiNote internal api, do not use
     */
    @ApiStatus.Internal
    public static void setPluginHost(JavaPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin cannot be null");
        Preconditions.checkArgument(pluginHost == null, "plugin host already set");

        pluginHost = plugin;
    }

    /**
     * Get the first available protection module
     *
     * @return the first available protection module
     */
    @Nullable
    public static IProtectionModule getFirstAvailableModule() {
        return (IProtectionModule) modules.toArray()[0];
    }

    /**
     * Find the protection module by plugin name
     *
     * @param pluginName the plugin name
     * @return the protection module
     */
    @Nullable
    public static IProtectionModule getModuleByPluginName(String pluginName) {
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

    public static boolean isInProtectionRange(Location location) {
        for (IProtectionModule module : modules) {
            if (module.isInProtectionRange(location)) {
                return true;
            }
        }

        for (IBlockProtectionModule module : blockModules) {
            if (module.isProtected(location)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get all available protection modules.
     *
     * @return a list contains all available protection modules.
     */
    public static @Unmodifiable @NotNull Set<IProtectionModule> getAllAvailableProtectionModules() {
        return Collections.unmodifiableSet(modules);
    }

    /**
     * Get all available block protection modules.
     *
     * @return a list contains all available protection modules.
     */
    public static @Unmodifiable @NotNull Set<IBlockProtectionModule> getAllAvailableBlockProtectionModules() {
        return Collections.unmodifiableSet(blockModules);
    }

    /**
     * Find the protection module that protects the protection range
     *
     * @param location the location in the protection range
     * @return the protection module
     */
    @Nullable
    public static IProtectionModule findModule(Location location) {
        for (IProtectionModule module : modules) {
            if (module.isInProtectionRange(location)) {
                return module;
            }
        }

        return null;
    }

    /**
     * Find the block protection module that protects the block
     *
     * @param block the block
     * @return the block protection module
     */
    @Nullable
    public static IBlockProtectionModule findBlockModule(Location block) {
        for (IBlockProtectionModule module : blockModules) {
            if (module.isProtected(block)) {
                return module;
            }
        }

        return null;
    }

    /**
     * Find the first matching module in registration order, dispatching each lookup according
     * to its verified asynchronous capability. Completion may occur on either thread; callers
     * must schedule Bukkit world/player mutations on the server thread and must not block it
     * with <code>Future.get()</code> or <code>CompletableFuture.join()</code>. Locations are copied before scheduling.
     */
    public static CompletableFuture<IProtectionModule> findModuleAsync(Location location) {
        return asyncChecks.findModule(location);
    }

    /**
     * @see {@link #findModuleAsync(Location)}
     */
    public static CompletableFuture<IBlockProtectionModule> findBlockModuleAsync(Location location) {
        return asyncChecks.findBlockModule(location);
    }

    /**
     * @see {@link #findModuleAsync(Location)}
     */
    public static CompletableFuture<Boolean> isInProtectionRangeAsync(Location location) {
        return asyncChecks.isInProtectionRange(location);
    }

    /**
     * Check the player's captured location without blocking for asynchronous checks.
     */
    public static CompletableFuture<Boolean> allowBreakAsync(Player player) {
        return asyncChecks.allow(player, CommonFlags.BREAK);
    }

    /**
     * Check both region and block protection at the supplied location.
     *
     * @see {@link #findModuleAsync(Location)}
     */
    public static CompletableFuture<Boolean> allowBreakAsync(Player player, Location block) {
        return asyncChecks.allow(player, block, CommonFlags.BREAK);
    }

    /**
     * Check the player's captured location without blocking for asynchronous checks.
     */
    public static CompletableFuture<Boolean> allowPlaceAsync(Player player) {
        return asyncChecks.allow(player, CommonFlags.PLACE);
    }

    /**
     * Check both region and block protection at the supplied location.
     *
     * @see #findModuleAsync(Location)
     */
    public static CompletableFuture<Boolean> allowPlaceAsync(Player player, Location block) {
        return asyncChecks.allow(player, block, CommonFlags.PLACE);
    }

    /**
     * Check the player's captured location without blocking for asynchronous checks.
     */
    public static CompletableFuture<Boolean> allowInteractAsync(Player player) {
        return asyncChecks.allow(player, CommonFlags.INTERACT);
    }

    /**
     * Check both region and block protection at the supplied location.
     *
     * @see #findModuleAsync(Location)
     */
    public static CompletableFuture<Boolean> allowInteractAsync(Player player, Location block) {
        return asyncChecks.allow(player, block, CommonFlags.INTERACT);
    }

    /**
     * @apiNote Internal lifecycle hook. Completes pending checks when scheduler tasks are cancelled on disable.
     */
    @ApiStatus.Internal
    public static void cancelPendingChecks() {
        asyncChecks.close();
    }

    /**
     * Check if the player can break a block
     *
     * @param player the player
     * @return true if the player can break a block, false otherwise
     */
    public static boolean allowBreak(Player player) {
        Location location = player.getLocation();
        IProtectionModule module = findModule(location);
        if (module == null) {
            for (IProtectionModule m2 : modules) {
                if (m2.isSupportGlobalFlags()) {
                    return Objects.requireNonNull(m2.getGlobalFlag(
                                    CommonFlags.BREAK, location.getWorld().getName()))
                            .toBooleanOrThrow();
                }
            }

            return true;
        }

        IProtectionRange info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        FlagState<?> flagState = info.getFlagState(CommonFlags.BREAK, player);
        if (flagState instanceof FlagStates.UnsupportedFlagState) {
            return true;
        }

        return flagState.toBooleanOrThrow();
    }

    /**
     * Check if the player can break a block
     *
     * @param player the player
     * @param block  the block
     * @return true if the player can break a block, false otherwise
     */
    public static boolean allowBreak(Player player, Location block) {
        IBlockProtectionModule module = findBlockModule(block);
        if (allowBreak(player)) {
            if (module == null) {
                return true;
            }

            return module.allowBreak(player, block);
        }

        return false;
    }

    /**
     * Check if the player can place a block
     *
     * @param player the player
     * @return true if the player can place a block, false otherwise
     */
    public static boolean allowPlace(Player player) {
        Location location = player.getLocation();
        IProtectionModule module = findModule(location);
        if (module == null) {
            for (IProtectionModule m2 : modules) {
                if (m2.isSupportGlobalFlags()) {
                    return Objects.requireNonNull(m2.getGlobalFlag(
                                    CommonFlags.PLACE, location.getWorld().getName()))
                            .toBooleanOrThrow();
                }
            }

            return true;
        }

        IProtectionRange info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        FlagState<?> flagState = info.getFlagState(CommonFlags.PLACE, player);
        if (flagState instanceof FlagStates.UnsupportedFlagState) {
            return true;
        }

        return flagState.toBooleanOrThrow();
    }

    /**
     * Check if the player can place a block
     *
     * @param player the player
     * @param block  the block
     * @return true if the player can place a block, false otherwise
     */
    public static boolean allowPlace(Player player, Location block) {
        IBlockProtectionModule module = findBlockModule(block);
        if (allowPlace(player)) {
            if (module == null) {
                return true;
            }

            return module.allowPlace(player, block);
        }

        return false;
    }

    /**
     * Check if the player can interact with a block
     *
     * @param player the player
     * @return true if the player can interact with a block
     */
    public static boolean allowInteract(Player player) {
        Location location = player.getLocation();
        IProtectionModule module = findModule(location);
        if (module == null) {
            for (IProtectionModule m2 : modules) {
                if (m2.isSupportGlobalFlags()) {
                    return Objects.requireNonNull(m2.getGlobalFlag(
                                    CommonFlags.INTERACT, location.getWorld().getName()))
                            .toBooleanOrThrow();
                }
            }

            return true;
        }

        IProtectionRange info = module.getProtectionRangeInfo(player);
        if (info == null) {
            return true;
        }

        FlagState<?> flagState = info.getFlagState(CommonFlags.INTERACT, player);
        if (flagState instanceof FlagStates.UnsupportedFlagState) {
            return true;
        }

        return flagState.toBooleanOrThrow();
    }

    /**
     * Check if the player can interact with the block
     *
     * @param player the player
     * @param block  the block
     * @return true if the player can interact with the block
     */
    public static boolean allowInteract(Player player, Location block) {
        IBlockProtectionModule module = findBlockModule(block);
        if (allowInteract(player)) {
            if (module == null) {
                return true;
            }

            return module.allowInteract(player, block);
        }

        return false;
    }

    /**
     * Get handlers by specific class
     *
     * @return handlers
     */
    public static <T extends AHandler> List<T> getHandlers(Class<T> handlerClass) {
        return handlers.stream()
                .filter(h -> h.getClass().isAssignableFrom(handlerClass))
                .map(t -> (T) t)
                .collect(Collectors.toList());
    }
}
