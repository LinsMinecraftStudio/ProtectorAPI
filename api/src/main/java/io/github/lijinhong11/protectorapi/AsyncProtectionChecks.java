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

import static java.util.concurrent.CompletableFuture.completedFuture;

import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IChunkProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Composes ordered protection checks while scheduling each operation on its supported thread.
 */
final class AsyncProtectionChecks {
    private final Collection<IProtectionModule> modules;
    private final Collection<IBlockProtectionModule> blockModules;
    private final Collection<IChunkProtectionModule> chunkModules;
    private final ProtectionCheckScheduler scheduler = new ProtectionCheckScheduler();

    AsyncProtectionChecks(Collection<IProtectionModule> modules, Collection<IBlockProtectionModule> blockModules) {
        this(modules, blockModules, java.util.List.of());
    }

    AsyncProtectionChecks(
            Collection<IProtectionModule> modules,
            Collection<IBlockProtectionModule> blockModules,
            Collection<IChunkProtectionModule> chunkModules) {
        this.modules = modules;
        this.blockModules = blockModules;
        this.chunkModules = chunkModules;
    }

    CompletableFuture<IProtectionModule> findModule(Location location) {
        Location snapshot = location.clone();
        return scheduler.execute(request -> findModule(request, snapshot));
    }

    private CompletableFuture<IProtectionModule> findModule(
            ProtectionCheckScheduler.Request<?> request, Location location) {
        return findFirst(
                modules,
                module -> request.submit(
                        module.supportsAsync(ProtectionCheck.LOOKUP),
                        () -> module.isInProtectionRange(location.clone())));
    }

    CompletableFuture<IBlockProtectionModule> findBlockModule(Location location) {
        Location snapshot = location.clone();
        return scheduler.execute(request -> findBlockModule(request, snapshot));
    }

    CompletableFuture<IChunkProtectionModule> findChunkModule(World world, int chunkX, int chunkZ) {
        return scheduler.execute(request -> findChunkModule(request, world, chunkX, chunkZ));
    }

    private CompletableFuture<IChunkProtectionModule> findChunkModule(
            ProtectionCheckScheduler.Request<?> request, World world, int chunkX, int chunkZ) {
        return findFirst(
                chunkModules,
                module -> request.submit(
                        module.supportsAsync(ProtectionCheck.CHUNK_LOOKUP),
                        () -> module.isProtected(world, chunkX, chunkZ)));
    }

    CompletableFuture<Boolean> isChunkProtected(World world, int chunkX, int chunkZ) {
        return findChunkModule(world, chunkX, chunkZ).thenApply(Objects::nonNull);
    }

    private CompletableFuture<IBlockProtectionModule> findBlockModule(
            ProtectionCheckScheduler.Request<?> request, Location location) {
        return findFirst(
                blockModules,
                module -> request.submit(
                        module.supportsAsync(ProtectionCheck.LOOKUP), () -> module.isProtected(location.clone())));
    }

    CompletableFuture<Boolean> isInProtectionRange(Location location) {
        Location snapshot = location.clone();
        return scheduler.execute(request -> findModule(request, snapshot)
                .thenCompose(module -> module != null
                        ? completedFuture(true)
                        : findBlockModule(request, snapshot).thenApply(Objects::nonNull)));
    }

    CompletableFuture<Boolean> allow(Player player, CommonFlags flag) {
        // Player location capture always belongs on the server thread.
        return scheduler.execute(
                request -> request.submit(false, () -> player.getLocation().clone())
                        .thenCompose(location -> allowRegion(request, player, location, flag)));
    }

    CompletableFuture<Boolean> allow(Player player, Location block, CommonFlags flag) {
        Location snapshot = block.clone();
        return scheduler.execute(request -> allowRegion(request, player, snapshot, flag)
                .thenCompose(
                        allowed -> allowed ? allowBlock(request, player, snapshot, flag) : completedFuture(false)));
    }

    private CompletableFuture<Boolean> allowRegion(
            ProtectionCheckScheduler.Request<?> request, Player player, Location location, CommonFlags flag) {
        return findModule(request, location)
                .thenCompose(module -> module == null
                        ? allowGlobal(request, location, flag)
                        : request.submit(
                                        module.supportsAsync(ProtectionCheck.LOOKUP),
                                        () -> module.getProtectionRangeInfo(location.clone()))
                                .thenCompose(range -> allowRange(request, module, range, player, flag)));
    }

    private CompletableFuture<Boolean> allowRange(
            ProtectionCheckScheduler.Request<?> request,
            IProtectionModule module,
            IProtectionRange range,
            Player player,
            CommonFlags flag) {
        if (range == null) {
            return completedFuture(true);
        }
        return request.submit(module.supportsAsync(ProtectionCheck.FLAGS), () -> {
            FlagState<?> state = range.getFlagState(flag, player);
            return state instanceof FlagStates.UnsupportedFlagState || state.toBooleanOrThrow();
        });
    }

    private CompletableFuture<Boolean> allowGlobal(
            ProtectionCheckScheduler.Request<?> request, Location location, CommonFlags flag) {
        return findFirst(
                        modules,
                        module -> request.submit(
                                module.supportsAsync(ProtectionCheck.GLOBAL_FLAGS), module::isSupportGlobalFlags))
                .thenCompose(module -> allowGlobalFlag(request, module, location, flag));
    }

    private CompletableFuture<Boolean> allowGlobalFlag(
            ProtectionCheckScheduler.Request<?> request,
            IProtectionModule module,
            Location location,
            CommonFlags flag) {
        if (module == null) {
            return completedFuture(true);
        }
        return request.submit(false, () -> location.getWorld().getName())
                .thenCompose(world -> request.submit(
                        module.supportsAsync(ProtectionCheck.GLOBAL_FLAGS),
                        () -> Objects.requireNonNull(module.getGlobalFlag(flag, world))
                                .toBooleanOrThrow()));
    }

    private CompletableFuture<Boolean> allowBlock(
            ProtectionCheckScheduler.Request<?> request, Player player, Location location, CommonFlags flag) {
        return findBlockModule(request, location).thenCompose(module -> {
            if (module == null) {
                return completedFuture(true);
            }
            return request.submit(
                    module.supportsAsync(ProtectionCheck.FLAGS),
                    () -> checkBlockFlag(module, player, location.clone(), flag));
        });
    }

    private static boolean checkBlockFlag(
            IBlockProtectionModule module, Player player, Location location, CommonFlags flag) {
        return switch (flag) {
            case BREAK -> module.allowBreak(player, location);
            case PLACE -> module.allowPlace(player, location);
            case INTERACT -> module.allowInteract(player, location);
            default -> throw new IllegalArgumentException("Unsupported permission check: " + flag);
        };
    }

    private static <T> CompletableFuture<T> findFirst(
            Collection<T> candidates, Function<T, CompletableFuture<Boolean>> matches) {
        CompletableFuture<T> result = completedFuture(null);
        // Snapshot registration order and only invoke a check if earlier candidates did not match.
        for (T candidate : new ArrayList<>(candidates)) {
            result = result.thenCompose(found -> found != null
                    ? completedFuture(found)
                    : matches.apply(candidate).thenApply(match -> match ? candidate : null));
        }
        return result;
    }
}
