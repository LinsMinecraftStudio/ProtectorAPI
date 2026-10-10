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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;

class AsyncProtectionChecksTest {
    private static final JavaPlugin HOST = mock(JavaPlugin.class);
    private final Queue<Runnable> mainTasks = new ArrayDeque<>();
    private final Queue<Runnable> asyncTasks = new ArrayDeque<>();
    private final List<BukkitTask> scheduledTasks = new ArrayList<>();
    private final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    private final World world = mock(World.class);
    private final Player player = mock(Player.class);
    private final Location location = new Location(world, 10, 64, 20);
    private MockedStatic<Bukkit> bukkit;
    private boolean primary = true;

    @BeforeAll
    static void initializeHost() {
        ProtectorAPI.setPluginHost(HOST);
    }

    @BeforeEach
    void setUp() throws Exception {
        clearModules("modules");
        clearModules("blockModules");
        when(HOST.isEnabled()).thenReturn(true);
        when(world.getName()).thenAnswer(invocation -> {
            assertTrue(primary, "Bukkit world access must stay on the server thread");
            return "world";
        });
        when(player.getLocation()).thenAnswer(invocation -> {
            assertTrue(primary, "Player location must be captured on the server thread");
            return location;
        });
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::isPrimaryThread).thenAnswer(invocation -> primary);
        bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
        when(scheduler.runTask(eq(HOST), any(Runnable.class))).thenAnswer(invocation -> {
            mainTasks.add(invocation.getArgument(1));
            return newTask();
        });
        when(scheduler.runTaskAsynchronously(eq(HOST), any(Runnable.class))).thenAnswer(invocation -> {
            asyncTasks.add(invocation.getArgument(1));
            return newTask();
        });
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    private static void clearModules(String name) throws Exception {
        Field field = ProtectorAPI.class.getDeclaredField(name);
        field.setAccessible(true);
        ((Set<?>) field.get(null)).clear();
    }

    private BukkitTask newTask() {
        BukkitTask task = mock(BukkitTask.class);
        scheduledTasks.add(task);
        return task;
    }

    private void drainTasks() {
        int count = 0;
        while (!mainTasks.isEmpty() || !asyncTasks.isEmpty()) {
            assertTrue(count++ < 100, "Unexpected scheduling loop");
            primary = mainTasks.isEmpty();
            if (!mainTasks.isEmpty()) {
                primary = true;
                mainTasks.remove().run();
            } else {
                primary = false;
                asyncTasks.remove().run();
            }
        }
        primary = true;
    }

    @Test
    void capabilitiesDefaultToSynchronous() {
        IProtectionModule region = mock(IProtectionModule.class, CALLS_REAL_METHODS);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class, CALLS_REAL_METHODS);
        for (ProtectionCheck check : ProtectionCheck.values()) {
            assertFalse(region.supportsAsync(check));
            assertFalse(block.supportsAsync(check));
        }
    }

    @Test
    void lookupReturnsWithoutWaitingAndPreservesOrderAndLocation() {
        IProtectionModule first = mock(IProtectionModule.class);
        IProtectionModule second = mock(IProtectionModule.class);
        IProtectionModule third = mock(IProtectionModule.class);
        when(first.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(first.isInProtectionRange(any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            Location copy = invocation.getArgument(0);
            assertEquals(10, copy.getBlockX());
            copy.setX(999);
            return false;
        });
        when(second.isInProtectionRange(any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary);
            assertEquals(10, ((Location) invocation.getArgument(0)).getBlockX());
            return true;
        });
        ProtectorAPI.register(first);
        ProtectorAPI.register(second);
        ProtectorAPI.register(third);

        CompletableFuture<IProtectionModule> result = ProtectorAPI.findModuleAsync(location);
        location.setX(500);
        assertFalse(result.isDone());
        verify(first, never()).isInProtectionRange(any(Location.class));
        drainTasks();

        assertSame(second, result.join());
        verify(third, never()).isInProtectionRange(any(Location.class));
    }

    @ParameterizedTest
    @EnumSource(
            value = CommonFlags.class,
            names = {"BREAK", "PLACE", "INTERACT"})
    void permissionChecksDispatchEachStageAndUseTargetLocation(CommonFlags flag) {
        IProtectionModule region = mock(IProtectionModule.class);
        IProtectionRange range = mock(IProtectionRange.class);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(region.isInProtectionRange(any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            assertEquals(10, ((Location) invocation.getArgument(0)).getBlockX());
            return true;
        });
        when(region.getProtectionRangeInfo(any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            return range;
        });
        doAnswer(invocation -> {
                    assertTrue(primary, "Async lookup must not imply async flags");
                    return FlagStates.ALLOW;
                })
                .when(range)
                .getFlagState(flag, player);
        when(block.isProtected(any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary);
            return true;
        });
        when(block.supportsAsync(ProtectionCheck.FLAGS)).thenReturn(true);
        when(block.allowBreak(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            return false;
        });
        when(block.allowPlace(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            return false;
        });
        when(block.allowInteract(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            return false;
        });
        ProtectorAPI.register(region);
        ProtectorAPI.register(block);

        CompletableFuture<Boolean> result =
                switch (flag) {
                    case BREAK -> ProtectorAPI.allowBreakAsync(player, location);
                    case PLACE -> ProtectorAPI.allowPlaceAsync(player, location);
                    case INTERACT -> ProtectorAPI.allowInteractAsync(player, location);
                    default -> throw new AssertionError(flag);
                };
        assertFalse(result.isDone());
        drainTasks();
        assertFalse(result.join());
        verify(player, never()).getLocation();
    }

    @Test
    void playerLocationAndGlobalFlagsReturnToMainThread() {
        IProtectionModule region = mock(IProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(region.isSupportGlobalFlags()).thenAnswer(invocation -> {
            assertTrue(primary);
            return true;
        });
        doAnswer(invocation -> {
                    assertTrue(primary);
                    return FlagStates.DENY;
                })
                .when(region)
                .getGlobalFlag(CommonFlags.BREAK, "world");
        ProtectorAPI.register(region);
        primary = false;

        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player);
        assertFalse(result.isDone());
        verify(player, never()).getLocation();
        drainTasks();
        assertFalse(result.join());
        verify(player).getLocation();
    }

    @ParameterizedTest
    @EnumSource(
            value = CommonFlags.class,
            names = {"BREAK", "PLACE", "INTERACT"})
    void asyncBlockLookupReturnsToServerThreadForPermissions(CommonFlags flag) {
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(block.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(block.isProtected(any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary, "Verified block lookups should run asynchronously");
            return true;
        });
        when(block.allowBreak(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary, "Lookup support must not enable async permission checks");
            return false;
        });
        when(block.allowPlace(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary, "Lookup support must not enable async permission checks");
            return false;
        });
        when(block.allowInteract(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary, "Lookup support must not enable async permission checks");
            return false;
        });
        ProtectorAPI.register(block);

        CompletableFuture<Boolean> result =
                switch (flag) {
                    case BREAK -> ProtectorAPI.allowBreakAsync(player, location);
                    case PLACE -> ProtectorAPI.allowPlaceAsync(player, location);
                    case INTERACT -> ProtectorAPI.allowInteractAsync(player, location);
                    default -> throw new AssertionError(flag);
                };
        assertFalse(result.isDone());
        drainTasks();
        assertFalse(result.join());
    }

    @Test
    void regionDenialShortCircuitsBlockChecks() {
        IProtectionModule region = mock(IProtectionModule.class);
        IProtectionRange range = mock(IProtectionRange.class);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(region.isInProtectionRange(any(Location.class))).thenReturn(true);
        when(region.getProtectionRangeInfo(any(Location.class))).thenReturn(range);
        doReturn(FlagStates.DENY).when(range).getFlagState(CommonFlags.PLACE, player);
        ProtectorAPI.register(region);
        ProtectorAPI.register(block);

        assertFalse(ProtectorAPI.allowPlaceAsync(player, location).join());
        verify(block, never()).isProtected(any(Location.class));
    }

    @Test
    void unsupportedFlagAllowsAndLookupFallsBackToBlockModules() {
        IProtectionModule region = mock(IProtectionModule.class);
        IProtectionRange range = mock(IProtectionRange.class);
        when(region.isInProtectionRange(any(Location.class))).thenReturn(true);
        when(region.getProtectionRangeInfo(any(Location.class))).thenReturn(range);
        doReturn(FlagStates.UNSUPPORTED).when(range).getFlagState(CommonFlags.INTERACT, player);
        ProtectorAPI.register(region);
        assertTrue(ProtectorAPI.allowInteractAsync(player, location).join());

        when(region.isInProtectionRange(any(Location.class))).thenReturn(false);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(block.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(block.isProtected(any(Location.class))).thenReturn(true);
        ProtectorAPI.register(block);
        CompletableFuture<Boolean> result = ProtectorAPI.isInProtectionRangeAsync(location);
        assertFalse(result.isDone());
        drainTasks();
        assertTrue(result.join());
    }

    @Test
    void moduleFailureIsExceptionalAndNeverBecomesAnAllow() {
        IProtectionModule region = mock(IProtectionModule.class);
        IllegalStateException failure = new IllegalStateException("plugin check failed");
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(region.isInProtectionRange(any(Location.class))).thenThrow(failure);
        ProtectorAPI.register(region);

        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player, location);
        drainTasks();
        assertSame(
                failure, assertThrows(CompletionException.class, result::join).getCause());
    }

    @Test
    void rejectedTasksAndDisableCompletePendingFutures() {
        ProtectionCheckScheduler dispatcher = new ProtectionCheckScheduler();
        Runnable check = mock(Runnable.class);
        CompletableFuture<Boolean> pending = dispatcher.execute(request -> request.submit(true, () -> {
            check.run();
            return true;
        }));
        dispatcher.close();
        assertTrue(pending.isCancelled());
        assertTrue(
                dispatcher.execute(request -> request.submit(true, () -> true)).isCancelled());
        drainTasks();
        verifyNoInteractions(check);

        IllegalStateException rejected = new IllegalStateException("scheduler stopped");
        when(scheduler.runTaskAsynchronously(eq(HOST), any(Runnable.class))).thenThrow(rejected);
        CompletableFuture<Boolean> result =
                new ProtectionCheckScheduler().execute(request -> request.submit(true, () -> true));
        assertSame(
                rejected, assertThrows(CompletionException.class, result::join).getCause());
    }

    @Test
    void noModulesAllowsWithoutScheduling() {
        assertTrue(ProtectorAPI.allowBreakAsync(player, location).join());
        assertFalse(ProtectorAPI.isInProtectionRangeAsync(location).join());
        verifyNoInteractions(scheduler);
    }

    @ParameterizedTest
    @EnumSource(
            value = CommonFlags.class,
            names = {"BREAK", "PLACE", "INTERACT"})
    void cancellingPermissionRequestStopsQueuedLookupAndLaterModules(CommonFlags flag) {
        IProtectionModule first = mock(IProtectionModule.class);
        IProtectionModule second = mock(IProtectionModule.class);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(first.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        ProtectorAPI.register(first);
        ProtectorAPI.register(second);
        ProtectorAPI.register(block);

        CompletableFuture<Boolean> result = permissionRequest(flag);
        assertTrue(result.cancel(false));
        verify(scheduledTasks.get(0)).cancel();
        drainTasks();

        assertTrue(result.isCancelled());
        verify(first, never()).isInProtectionRange(any(Location.class));
        verifyNoInteractions(second, block);
    }

    @ParameterizedTest
    @EnumSource(
            value = CommonFlags.class,
            names = {"BREAK", "PLACE", "INTERACT"})
    void cancellingBetweenLookupAndFlagsStopsServerThreadPermissions(CommonFlags flag) {
        IProtectionModule region = mock(IProtectionModule.class);
        IProtectionRange range = mock(IProtectionRange.class);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(region.isInProtectionRange(any(Location.class))).thenReturn(true);
        when(region.getProtectionRangeInfo(any(Location.class))).thenReturn(range);
        ProtectorAPI.register(region);
        ProtectorAPI.register(block);

        CompletableFuture<Boolean> result = permissionRequest(flag);
        primary = false;
        asyncTasks.remove().run();
        asyncTasks.remove().run();
        assertEquals(1, mainTasks.size());
        assertTrue(result.cancel(false));
        verify(scheduledTasks.get(2)).cancel();
        drainTasks();

        verifyNoInteractions(range, block);
        verify(scheduledTasks.get(0), never()).cancel();
        verify(scheduledTasks.get(1), never()).cancel();
    }

    @Test
    void cancellingOneLookupDoesNotCancelAnotherRequest() {
        IProtectionModule region = mock(IProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(region.isInProtectionRange(any(Location.class))).thenReturn(true);
        ProtectorAPI.register(region);

        CompletableFuture<IProtectionModule> cancelled = ProtectorAPI.findModuleAsync(location);
        CompletableFuture<IProtectionModule> remaining = ProtectorAPI.findModuleAsync(location);
        cancelled.cancel(true);
        drainTasks();

        assertTrue(cancelled.isCancelled());
        assertSame(region, remaining.join());
        verify(region).isInProtectionRange(any(Location.class));
        verify(scheduledTasks.get(0)).cancel();
        verify(scheduledTasks.get(1), never()).cancel();
    }

    @Test
    void cancellingProtectionRangeRequestStopsBlockFallback() {
        IProtectionModule region = mock(IProtectionModule.class);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(block.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        ProtectorAPI.register(region);
        ProtectorAPI.register(block);

        CompletableFuture<Boolean> result = ProtectorAPI.isInProtectionRangeAsync(location);
        primary = false;
        asyncTasks.remove().run();
        assertEquals(1, asyncTasks.size());
        result.cancel(false);
        drainTasks();

        assertTrue(result.isCancelled());
        verify(block, never()).isProtected(any(Location.class));
        verify(scheduledTasks.get(1)).cancel();
    }

    @Test
    void cancellingPlayerRequestStopsQueuedLocationCapture() {
        primary = false;
        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player);
        assertTrue(result.cancel(false));
        drainTasks();

        verify(player, never()).getLocation();
        verify(scheduledTasks.get(0)).cancel();
    }

    @Test
    void cancellingGlobalFlagRequestStopsQueuedFlagRead() {
        IProtectionModule region = mock(IProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.GLOBAL_FLAGS)).thenReturn(true);
        when(region.isSupportGlobalFlags()).thenReturn(true);
        ProtectorAPI.register(region);

        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player, location);
        primary = false;
        asyncTasks.remove().run();
        primary = true;
        mainTasks.remove().run();
        assertEquals(1, asyncTasks.size());
        result.cancel(false);
        drainTasks();

        verify(region, never()).getGlobalFlag(any(CommonFlags.class), anyString());
        verify(scheduledTasks.get(2)).cancel();
    }

    @Test
    void disableCancelsEntireRequestsAndRejectsEmptyChecks() {
        IProtectionModule region = mock(IProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        List<IProtectionModule> modules = new ArrayList<>(List.of(region));
        AsyncProtectionChecks checks = new AsyncProtectionChecks(modules, List.of());
        CompletableFuture<Boolean> pending = checks.allow(player, location, CommonFlags.BREAK);

        checks.close();
        assertTrue(pending.isCancelled());
        modules.clear();
        assertTrue(checks.findModule(location).isCancelled());
        assertTrue(checks.findBlockModule(location).isCancelled());
        assertTrue(checks.isInProtectionRange(location).isCancelled());
        assertTrue(checks.allow(player, location, CommonFlags.PLACE).isCancelled());
        assertTrue(checks.allow(player, CommonFlags.INTERACT).isCancelled());
        drainTasks();

        verify(region, never()).isInProtectionRange(any(Location.class));
        verify(scheduledTasks.get(0)).cancel();
        assertEquals(1, scheduledTasks.size());
    }

    @Test
    void disabledHostRejectsEmptyChecks() {
        when(HOST.isEnabled()).thenReturn(false);
        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player, location);
        assertInstanceOf(
                IllegalStateException.class,
                assertThrows(CompletionException.class, result::join).getCause());
        verifyNoInteractions(scheduler);
    }

    @Test
    void disableDuringDispatchCancelsTaskReturnedAfterClose() {
        ProtectionCheckScheduler dispatcher = new ProtectionCheckScheduler();
        BukkitTask task = mock(BukkitTask.class);
        Runnable check = mock(Runnable.class);
        when(scheduler.runTaskAsynchronously(eq(HOST), any(Runnable.class))).thenAnswer(invocation -> {
            asyncTasks.add(invocation.getArgument(1));
            dispatcher.close();
            return task;
        });

        CompletableFuture<Boolean> result = dispatcher.execute(request -> request.submit(true, () -> {
            check.run();
            return true;
        }));
        assertTrue(result.isCancelled());
        verify(task).cancel();
        drainTasks();
        verifyNoInteractions(check);
    }

    @Test
    void cancellationDuringRunningCheckPreventsLaterStages() {
        IProtectionModule region = mock(IProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        ProtectorAPI.register(region);
        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player, location);
        when(region.isInProtectionRange(any(Location.class))).thenAnswer(invocation -> {
            result.cancel(false);
            return true;
        });

        drainTasks();
        assertTrue(result.isCancelled());
        verify(region, never()).getProtectionRangeInfo(any(Location.class));
        assertEquals(1, scheduledTasks.size());
    }

    @Test
    void timeoutCancelsQueuedChecks() throws Exception {
        IProtectionModule region = mock(IProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        ProtectorAPI.register(region);
        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player, location);

        result.orTimeout(1, TimeUnit.MILLISECONDS);
        assertInstanceOf(
                TimeoutException.class,
                assertThrows(ExecutionException.class, () -> result.get(5, TimeUnit.SECONDS))
                        .getCause());
        verify(scheduledTasks.get(0), timeout(1000)).cancel();
        drainTasks();
        verify(region, never()).isInProtectionRange(any(Location.class));
    }

    @Test
    void disableWhileRequestIsBeingComposedStopsNewStages() throws Exception {
        ProtectionCheckScheduler dispatcher = new ProtectionCheckScheduler();
        CountDownLatch composing = new CountDownLatch(1);
        CompletableFuture<Void> resume = new CompletableFuture<>();
        Runnable check = mock(Runnable.class);
        CompletableFuture<CompletableFuture<Boolean>> invocation =
                CompletableFuture.supplyAsync(() -> dispatcher.execute(request -> {
                    composing.countDown();
                    resume.join();
                    return request.submit(true, () -> {
                        check.run();
                        return true;
                    });
                }));
        try {
            assertTrue(composing.await(5, TimeUnit.SECONDS));
            dispatcher.close();
            resume.complete(null);
            assertTrue(invocation.get(5, TimeUnit.SECONDS).isCancelled());
            verifyNoInteractions(check, scheduler);
        } finally {
            resume.complete(null);
            dispatcher.close();
        }
    }

    private CompletableFuture<Boolean> permissionRequest(CommonFlags flag) {
        return switch (flag) {
            case BREAK -> ProtectorAPI.allowBreakAsync(player, location);
            case PLACE -> ProtectorAPI.allowPlaceAsync(player, location);
            case INTERACT -> ProtectorAPI.allowInteractAsync(player, location);
            default -> throw new AssertionError(flag);
        };
    }
}
